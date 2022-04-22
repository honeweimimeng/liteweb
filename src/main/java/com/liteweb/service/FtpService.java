package com.liteweb.service;

import com.liteweb.connector.ServletConnector;
import com.liteweb.container.ServletContainer;

import java.nio.channels.Channel;
import java.util.List;

/**
 * @author Hone
 */
public class FtpService extends SocketService{
    @Override
    public Channel getChannel() {
        return null;
    }

    @Override
    public ServiceHandler serviceHandler(Object... objects) {
        return null;
    }

    @Override
    ServletContainer servletContainer() {
        return null;
    }

    @Override
    List<ServletConnector> servletConnectors() {
        return null;
    }

    @Override
    public Object accept(Channel channel) {
        return null;
    }
}
