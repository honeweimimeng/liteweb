package com.liteweb.factory;

import com.liteweb.service.FtpService;
import com.liteweb.service.SocketService;

import java.nio.channels.Channel;

/**
 * @author Hone
 */
public class FtpServiceFactory extends ServiceFactory{
    @Override
    public SocketService production(Channel channel) {
        return new FtpService();
    }
}
