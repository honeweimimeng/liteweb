package com.liteweb.connector;

import com.liteweb.builder.HttpServletBuilder;
import com.liteweb.context.LiteWebContext;
import com.liteweb.entity.HttpFilter;
import com.liteweb.factory.LoggerFactory;
import com.liteweb.entity.HttpServletRequest;
import com.liteweb.entity.HttpServletResponse;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class HttpServletConnector extends ServletConnector{
    private static final Logger logger= LoggerFactory.createInfo("Servlet返回结果");
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;
    private final HttpFilter httpFilter;

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

    /**
     * HttpServlet组织器
     * @param servletBuilder
     * servlet建造者
     */
    public HttpServletConnector(HttpServletBuilder servletBuilder){
        super(servletBuilder.getChannel());
        //查找拦截器
        httpServletRequest=(HttpServletRequest)servletBuilder.buildRequest();
        httpServletRequest.setConnector(this);
        httpServletResponse=(HttpServletResponse)servletBuilder.buildResponse();
        httpServletResponse.setConnector(this);
        httpFilter=(HttpFilter) LiteWebContext.getInstance().getFilterRegister()
                .findByFilterPath(httpServletRequest.getRequestUri());
    }

    /**
     * 返回报文实体对应的缓冲区数组
     * @param channel 通信管道
     * @param messageBuffers 响应头，响应体
     */
    public void response(Channel channel, ByteBuffer[] messageBuffers){
        boolean isAsy = channel instanceof AsynchronousSocketChannel;
        long res= isAsy ? responseAsy((AsynchronousSocketChannel) channel,messageBuffers):
                responseSyn((SocketChannel)channel,messageBuffers);
        logger.info("管道返回码"+res);
    }

    /**
     * 异步响应
     * @param socketChannel 异步管道
     * @param messageBuffers 缓冲区数组
     * @return 写入大小
     */
    protected long responseAsy(AsynchronousSocketChannel socketChannel, ByteBuffer[] messageBuffers){
        WriteEntity writeEntity=new WriteEntity(0L,0L);
        ByteBuffer[][] loopBuffer=writeInBatches(messageBuffers);
        socketChannel.write(loopBuffer[0],0,loopBuffer[0].length,30,
                TimeUnit.SECONDS, writeEntity, new CompletionHandler<Long, Object>() {
                    @Override
                    public void completed(Long result, Object attachment) {
                        WriteEntity writes=(WriteEntity)attachment;
                        int index=(int)writes.index;
                        long allSize = calAllSize(loopBuffer[index]);
                        if(result<allSize){
                            writes.now+=result;
                            socketChannel.write(loopBuffer[index],0,loopBuffer[index].length,30
                                    ,TimeUnit.SECONDS,writes,this);
                            return;
                        }
                        //已经写完
                        if(++index == loopBuffer.length){
                            try {
                                socketChannel.close();
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                            return;
                        }
                        socketChannel.write(loopBuffer[index],0,loopBuffer[index].length,30
                                ,TimeUnit.SECONDS,index,this);
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        exc.printStackTrace();
                        try {
                            socketChannel.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                });
        return 0;
    }

    /**
     * 同步写入管道
     * @param socketChannel 同步管道
     * @param messageBuffers 缓冲区数组
     * @return 写入大小
     */
    protected long responseSyn(SocketChannel socketChannel, ByteBuffer[] messageBuffers){
        long count=0;
        try {
            ByteBuffer[][] byteBuffers=writeInBatches(messageBuffers);
            for (ByteBuffer[] writeByteBuffer : byteBuffers) {
                long allSize = calAllSize(writeByteBuffer);
                long nowWrite=socketChannel.write(writeByteBuffer);
                while(nowWrite<allSize){
                    nowWrite+=socketChannel.write(writeByteBuffer);
                }
                count+=nowWrite;
            }
            socketChannel.close();
        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException("返回数据错误");
        }
        return (int) count;
    }

    private long calAllSize(ByteBuffer[] byteBuffers){
        return Arrays.stream(byteBuffers).mapToLong(Buffer::remaining).sum();
    }

    /**
     * 对缓冲区重新规划
     * @param messageBuffers 缓冲区数组
     * @return 重新规划后的缓冲区数组
     */
    private ByteBuffer[][] writeInBatches(ByteBuffer[] messageBuffers){
        if(messageBuffers.length>15){
            int count=messageBuffers.length/15;
            int remain=messageBuffers.length%15;
            if(remain>0){
                count+=1;
            }
            ByteBuffer[][] byteBuffers=new ByteBuffer[count][];
            for (int i = 0; i <count ; i++) {
                int size = i+1==count&&remain>0 ?remain:15;
                ByteBuffer[] byteBuffersArrInner=new ByteBuffer[size];
                for (int j = 0; j < 15&&i*15+j<messageBuffers.length; j++) {
                    byteBuffersArrInner[j]=messageBuffers[i*15+j];
                }
                byteBuffers[i]=byteBuffersArrInner;
            }
            return byteBuffers;
        }
        return new ByteBuffer[][]{messageBuffers};
    }

    public HttpFilter getHttpFilter() {
        return httpFilter;
    }

    static class WriteEntity{
        long index;
        long now;

        public WriteEntity(Long index,Long now){
            this.index=index;
            this.now=now;
        }
    }
}
