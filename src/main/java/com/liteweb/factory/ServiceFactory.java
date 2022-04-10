package com.liteweb.factory;

import com.liteweb.service.SocketService;

import java.nio.channels.Channel;

public abstract class ServiceFactory {
    public String name="未知服务";
    public abstract SocketService production(Channel channel);
}