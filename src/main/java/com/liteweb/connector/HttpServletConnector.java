package com.liteweb.connector;

import com.liteweb.builder.HttpServletBuilder;
import com.liteweb.context.LiteWebContext;
import com.liteweb.entity.HttpFilter;
import com.liteweb.factory.LoggerFactory;
import com.liteweb.entity.HttpServletRequest;
import com.liteweb.entity.HttpServletResponse;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.SocketChannel;
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
        //查找拦截器
        httpServletRequest=(HttpServletRequest)servletBuilder.buildRequest();
        httpServletRequest.setConnector(this);
        httpServletResponse=(HttpServletResponse)servletBuilder.buildResponse();
        httpServletResponse.setConnector(this);
        httpFilter=(HttpFilter) LiteWebContext.getInstance().getFilterRegister()
                .findByFilterPath(httpServletRequest.getRequestURI());
    }

    /**
     * 返回报文实体对应的缓冲区数组
     * @param channel 通信管道
     * @param messageBuffers 响应头，响应体
     */
    public void response(Channel channel, ByteBuffer[] messageBuffers){
        boolean isAsy = channel instanceof AsynchronousSocketChannel;
        int res= isAsy ? responseAsy((AsynchronousSocketChannel) channel,messageBuffers):
                responseSyn((SocketChannel)channel,messageBuffers);
        logger.info("管道返回码"+res);
    }

    protected int responseAsy(AsynchronousSocketChannel socketChannel, ByteBuffer[] messageBuffers){
        socketChannel.write(messageBuffers,0,messageBuffers.length,30,
                TimeUnit.SECONDS, null, new CompletionHandler<Long, Object>() {
            @Override
            public void completed(Long result, Object attachment) {
                try {
                    socketChannel.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
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

    protected int responseSyn(SocketChannel socketChannel, ByteBuffer[] messageBuffers){
        try {
            socketChannel.write(messageBuffers);
            socketChannel.close();
        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException("返回数据错误");
        }
        return 0;
    }

    public HttpFilter getHttpFilter() {
        return httpFilter;
    }
}
