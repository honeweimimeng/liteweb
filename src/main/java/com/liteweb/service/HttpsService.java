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
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

/**
 * 基于TLS+TCP(Socket/AsySocket)的Https协议
 */
public class HttpsService extends HttpService{
    private static final Logger logger= LoggerFactory.createInfo("HttpsServlet预处理");
    private static final Logger hanShake_logger= LoggerFactory.createWarning("HttpsService握手警告");
    private long start;
    private SSLEngine engine;

    public HttpsService(Channel channel) {
        super(channel);
    }

    @Override
    public ServiceHandler serviceHandler() {
        return new Decor();
    }

    @Override
    public SocketChannel Accept(boolean isBlocking, SelectionKey key, Selector selector) throws Exception {
        SocketChannel socketChannel = super.Accept(isBlocking, key, selector);
        socketChannel.configureBlocking(isBlocking);
        initSSL(socketChannel);
        return socketChannel;
    }

    @Override
    public void Accept(Channel channel){
        try {
            initSSL(channel);
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
     * @throws Exception
     */
    private void initSSL(Channel socketChannel) throws Exception {
        start=System.currentTimeMillis();
        engine=getContext().createSSLEngine();
        //服务模式
        engine.setUseClientMode(false);
        HandShake(engine,socketChannel);
    }

    /**
     * 获取SSL上下文
     * @return SSLContext
     */
    private SSLContext getContext() throws Exception{
        KeyStore ks = KeyStore.getInstance ( "JKS" );
        FileInputStream fis = new FileInputStream ( LiteWebConfig.ssl_certificateFile );
        char[] password = LiteWebConfig.ssl_certificatePwd.toCharArray();
        ks.load (fis,password);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance ( "SunX509" );
        kmf.init(ks,password);
        SSLContext sslContext = SSLContext.getInstance ( LiteWebConfig.ssl_protocol );
        sslContext.init ( kmf.getKeyManagers (), null,
                new SecureRandom());
        return sslContext;
    }

    /**
     * SSL握手
     * @param engine SSL自定义握手攻城器
     * @param channel 消息管道
     */
    private void HandShake(SSLEngine engine,Channel channel) throws Exception{
        //设置缓冲区
        SSLSession sslSession=engine.getSession();
        int appSize=sslSession.getApplicationBufferSize();
        int netSize=sslSession.getPacketBufferSize();
        ByteBuffer inApp = ByteBuffer.allocateDirect(appSize+10);
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
     * @throws Exception
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
                case FINISHED:
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

        /**
         * 增加Https证书验证处理
         * @param channel 通信管道
         */
        @Override
        public void invokeToInfo(Channel channel) {
            try {
                socketChannel=channel;
                //等待握手完成
                HttpServletConnector httpServletConnector=
                        new HttpServletConnectorProxy(new HttpBuilderProxy(channel,engine),engine);
                HttpServletRequest request = realHttpHandler(httpServletConnector);
                logger.info("Method:"+request.getMethod()+" -- Path:"+request.getRequestURI()+" -- 耗费时间："+(System.currentTimeMillis()-start)+"ms");
            }catch (Exception e){
                e.printStackTrace();
                try {
                    channel.close();
                }catch (IOException cl_e){
                    cl_e.printStackTrace();
                }
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
         * @throws IOException
         * @throws ExecutionException
         * @throws InterruptedException
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
                ByteBuffer[] messageBuffers_res=new ByteBuffer[messageBuffers.length];
                for (int i = 0; i < messageBuffers.length; i++) {
                    ByteBuffer byteBuffer=ByteBuffer.allocateDirect(sslEngine.getSession().getPacketBufferSize());
                    byteBuffer.clear();
                    sslEngine.wrap(messageBuffers[i],byteBuffer);
                    byteBuffer.flip();
                    messageBuffers_res[i]=byteBuffer;
                }
                super.response(channel,messageBuffers_res);
            }catch (Exception e){
                throw new RuntimeException("响应报文加密错误");
            }
        }
    }
}
