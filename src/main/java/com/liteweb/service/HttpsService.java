package com.liteweb.service;

import com.liteweb.builder.HttpServletBuilder;
import com.liteweb.config.LiteWebConfig;
import com.liteweb.connector.HttpServletConnector;
import com.liteweb.entity.HttpServletRequest;
import com.liteweb.exception.ServerException;
import com.liteweb.factory.LoggerFactory;
import com.liteweb.util.ChannelUtil;
import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

/**
 * 基于TLS+TCP(Socket/AsySocket)的Https协议
 * @author Hone
 */
public class HttpsService extends HttpService{
    private static final Logger logger= LoggerFactory.createInfo("HttpsServlet预处理");
    private static final Logger hanShake_logger= LoggerFactory.createWarning("HttpsService握手警告");
    private long start;

    public HttpsService(Channel channel) {
        super(channel);
    }

    @Override
    public ServiceHandler serviceHandler(Object... objects) {
        return new Decor((SSLEngine) objects[0]);
    }

    @Override
    public void accept(boolean isBlocking, SelectionKey key, Selector selector) throws Exception {
        SocketChannel socketChannel=((ServerSocketChannel)key.channel()).accept();
        SSLEngine engine=initSSL(socketChannel);
        socketChannel.configureBlocking(isBlocking);
        socketChannel.register(selector, SelectionKey.OP_READ).attach(engine);
    }

    @Override
    public Object accept(Channel channel){
        try {
            return initSSL(channel);
        }catch (Exception e){
            try {
                channel.close();
            }catch (Exception ec){
                ec.printStackTrace();
            }
            throw new RuntimeException("SSL握手失败");
        }
    }

    /**
     * 初始化SSL
     * @param socketChannel 通信管道
     * @throws Exception 初始化SSL异常
     */
    private SSLEngine initSSL(Channel socketChannel) throws Exception {
        start=System.currentTimeMillis();
        SSLEngine engine=getContext().createSSLEngine();
        //服务模式
        engine.setUseClientMode(false);
        engine.setWantClientAuth(false);
        engine.setEnableSessionCreation(true);
        handShake(engine,socketChannel);
        return engine;
    }

    /**
     * 获取SSL上下文
     * @return SSLContext
     */
    private SSLContext getContext() throws Exception{
        KeyStore ks = KeyStore.getInstance ( "JKS" );
        FileInputStream fis = new FileInputStream ( LiteWebConfig.SSL_CERTIFICATE_FILE );
        char[] password = LiteWebConfig.SSL_CERTIFICATE_PWD.toCharArray();
        ks.load (fis,password);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance ( "SunX509" );
        kmf.init(ks,password);
        SSLContext sslContext = SSLContext.getInstance ( LiteWebConfig.SSL_PROTOCOL );
        sslContext.init ( kmf.getKeyManagers (), null,
                new SecureRandom());
        return sslContext;
    }

    /**
     * SSL握手
     * @param engine SSL自定义握手攻城器
     * @param channel 消息管道
     */
    private void handShake(SSLEngine engine, Channel channel) throws Exception{
        //设置缓冲区
        SSLSession sslSession=engine.getSession();
        int appSize=sslSession.getApplicationBufferSize();
        int netSize=sslSession.getPacketBufferSize();
        ByteBuffer inApp = ByteBuffer.allocateDirect(appSize);
        ByteBuffer inNet=ByteBuffer.allocateDirect(netSize);
        engine.beginHandshake();
        try {
            doHandShake(engine,channel,inApp,inNet);
        }catch (Exception e){
            hanShake_logger.warning(e.getMessage());
        }
    }

    /**
     * 握手动作
     * @param engine SSL引擎
     * @param channel 通信管道
     * @param appBuf 本地缓冲
     * @param netBuf 网络数据缓冲
     * @throws Exception SSL握手异常
     */
    private void doHandShake(SSLEngine engine,Channel channel,ByteBuffer appBuf,ByteBuffer netBuf) throws Exception {
        SSLEngineResult.HandshakeStatus hsStatus=engine.getHandshakeStatus();
        boolean handshakeDone = false;
        while (!handshakeDone) {
            switch (hsStatus){
                case NEED_UNWRAP:
                    netBuf.clear();
                    ChannelUtil.read(channel,netBuf);
                    netBuf.flip();
                    do {
                        engine.unwrap(netBuf, appBuf);
                        hsStatus = doRunnable(engine);
                    }
                    while(netBuf.hasRemaining() && hsStatus ==  SSLEngineResult.HandshakeStatus.NEED_UNWRAP);
                    break;
                case NEED_WRAP:
                    netBuf.clear();
                    engine.wrap(ByteBuffer.allocateDirect(0), netBuf);
                    netBuf.flip();
                    ChannelUtil.write(channel,netBuf);
                    hsStatus=doRunnable(engine);
                    break;
                case NEED_TASK:
                    hsStatus=doRunnable(engine);
                    break;
                case NOT_HANDSHAKING:
                    handshakeDone=true;
                    break;
                default:
                    break;
            }
        }
    }

    private SSLEngineResult.HandshakeStatus doRunnable(SSLEngine engine){
        Runnable task;
        while ((task = engine.getDelegatedTask()) != null)
        {
            task.run();
        }
        return engine.getHandshakeStatus();
    }

    /**
     * 装饰器类方法增强
     */
    class Decor implements ServiceHandler {
        private final SSLEngine engine;
        public Decor(SSLEngine engine){
            this.engine=engine;
        }
        /**
         * 增加Https证书验证处理
         * @param channel 通信管道
         */
        @Override
        public void invokeToInfo(Channel channel) {
            try {
                //等待握手完成
                HttpServletConnector httpServletConnector=
                        new HttpServletConnectorProxy(new HttpBuilderProxy(channel,engine),engine);
                HttpServletRequest request = realHttpHandler(httpServletConnector);
                logger.info("Method:"+request.getMethod()+" -- Path:"+request.getRequestUri()+" -- 耗费时间："+(System.currentTimeMillis()-start)+"ms");
            }catch (Exception e){
                throw new ServerException("确认是否配置XXX.keystore路径以及密码,也有可能客户端未此安装证书,或访问并非安全访问");
            }
        }
    }

    /**
     * Http报文建造者类的装饰器类
     */
    static class HttpBuilderProxy extends HttpServletBuilder {
        private final SSLEngine engine;
        private ByteBuffer unWarpData=null;
        public HttpBuilderProxy(Channel channel,SSLEngine engine) {
            super(channel);
            this.engine=engine;
        }

        /**
         * 读取Buffer数据方法，SSL数据需要解密
         * @param byteBuffer 字节缓冲区
         * @return 读取缓冲区大小
         * @throws IOException 读取异常
         * @throws ExecutionException 异步管道异常
         * @throws InterruptedException 异步管道异常
         */
        @Override
        protected int selChannelToBuffer(ByteBuffer byteBuffer) throws IOException, ExecutionException, InterruptedException {
            int loopReadSize;
            if(unWarpData==null){
                //初次读取，则解密全部数据
                ByteBuffer byteBuffer_wrap=ByteBuffer.allocateDirect(engine.getSession().getPacketBufferSize());
                super.selChannelToBuffer(byteBuffer_wrap);
                byteBuffer_wrap.flip();
                unWarpData=ByteBuffer.allocateDirect(engine.getSession().getApplicationBufferSize());
                engine.unwrap(byteBuffer_wrap,unWarpData);
                unWarpData.flip();
            }
            //复制到传入缓冲区
            loopReadSize= Math.min(unWarpData.remaining(), byteBuffer.remaining());
            byte[] bytes = new byte[loopReadSize];
            unWarpData.get(bytes,0,bytes.length);
            byteBuffer.put(bytes,0,bytes.length);
            return loopReadSize;
        }

        /**
         * 握手后读取数据方法，如果握手后1秒内新消息未到达则抛出超时
         * @param request 请求实体
         * @param byteBuffer 字节缓冲实例
         */
        @Override
        protected void readByteBuffer(HttpServletRequest request, ByteBuffer byteBuffer) {
            long start=System.currentTimeMillis();
            try {
                int size=selChannelToBuffer(byteBuffer);
                while (size<=0){
                    size=selChannelToBuffer(byteBuffer);
                    //1秒后数据未到达，抛出超时异常
                    if(System.currentTimeMillis()-start>1000){
                        throw new RuntimeException("握手后数据长时间未到达");
                    }
                }
            }catch (Exception e){
                try {
                    channel.close();
                }catch (Exception ec){
                    ec.printStackTrace();
                }
            }
            super.readByteBuffer(request, byteBuffer);
        }
    }

    /**
     * HttpServletConnector代理类
     */
    static class HttpServletConnectorProxy extends HttpServletConnector {
        private final SSLEngine sslEngine;
        /**
         * HttpServlet组织器
         *
         * @param servletBuilder servlet建造者
         */
        public HttpServletConnectorProxy(HttpServletBuilder servletBuilder,SSLEngine engine) {
            super(servletBuilder);
            this.sslEngine=engine;
        }

        /**
         * 重写返回分发器先加密再返回
         * @param channel Socket管道
         * @param messageBuffers 返回实体
         */
        @Override
        public void response(Channel channel, ByteBuffer[] messageBuffers) {
            try {
                List<ByteBuffer> list=new ArrayList<>();
                for (ByteBuffer messageBuffer : messageBuffers) {
                    while (messageBuffer.hasRemaining()) {
                        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(sslEngine.getSession().getPacketBufferSize());
                        byteBuffer.clear();
                        sslEngine.wrap(messageBuffer, byteBuffer);
                        byteBuffer.flip();
                        list.add(byteBuffer);
                    }
                }
                ByteBuffer[] byteBuffers=new ByteBuffer[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    byteBuffers[i]=list.get(i);
                }
                super.response(channel,byteBuffers);
            }catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException("响应报文加密错误");
            }
        }
    }
}
