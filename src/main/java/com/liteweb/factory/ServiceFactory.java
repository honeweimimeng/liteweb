package com.liteweb.factory;

import com.liteweb.service.SocketService;

import java.nio.channels.Channel;

/**
 * @author Hone
 */
public abstract class ServiceFactory {
    public String name="未知服务";

    /**
     * 服务生产方法
     * @param channel 通信管道
     * @return SocketService
     */
    public abstract SocketService production(Channel channel);
}