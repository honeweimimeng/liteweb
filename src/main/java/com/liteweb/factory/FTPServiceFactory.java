package com.liteweb.factory;

import com.liteweb.service.FTPService;
import com.liteweb.service.SocketService;

import java.nio.channels.Channel;

public class FTPServiceFactory extends ServiceFactory{
    @Override
    public SocketService production(Channel channel) {
        return new FTPService();
    }
}
