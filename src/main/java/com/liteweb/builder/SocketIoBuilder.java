package com.liteweb.builder;

import com.liteweb.exception.ServerException;
import com.liteweb.service.ServiceHandler;
import com.liteweb.service.SocketService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.Channel;
import java.nio.channels.ServerSocketChannel;

/**
 * @author Hone
 */
public abstract class SocketIoBuilder {
    private Channel channel;

    /**
     * 获取通信管道
     * @return Channel
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * 是否阻塞
     * @return boolean
     */
    abstract boolean isBlocking();

    /**
     * 是否异步
     * @return boolean
     */
    abstract boolean isAsync();

    /**
     * 装填IoSocket信息
     * @param socketService
     * WebIoSocket实体
     */
    public abstract void loadingIoSocket(SocketService socketService);

    /**
     * 构建基础Channel
     * @param inetSocketAddress 服务注册地址
     * @return Channel
     */
    public Channel buildChannel(InetSocketAddress inetSocketAddress){
        channel=isAsync() ? buildAsync(inetSocketAddress):buildSyn(inetSocketAddress);
        return channel;
    }

    /**
     * 创建异步管道
     * @return Channel
     */
    private Channel buildAsync(InetSocketAddress inetSocketAddress){
        AsynchronousServerSocketChannel channel;
        try {
            channel=AsynchronousServerSocketChannel.open().bind(inetSocketAddress);
        }catch (IOException e){
            throw new ServerException("异步管道开启失败"+e.getMessage());
        }
        return channel;
    }

    /**
     * 创建同步管道
     * @return Channel
     */
    private Channel buildSyn(InetSocketAddress inetSocketAddress){
        ServerSocketChannel channel;
        try {
            channel=ServerSocketChannel.open();
            channel.configureBlocking(isBlocking());
            channel.socket().bind(inetSocketAddress);
        }catch (IOException e){
            throw new ServerException("同步管道开启失败"+e.getMessage());
        }
        return channel;
    }
}
