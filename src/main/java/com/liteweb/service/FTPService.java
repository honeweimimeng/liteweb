package com.liteweb.service;

import com.liteweb.connector.ServletConnector;
import com.liteweb.container.ServletContainer;

import java.nio.channels.Channel;
import java.util.List;

public class FTPService extends SocketService{
    @Override
    public Channel getChannel() {
        return null;
    }

    @Override
    public Channel getSocketChannel() {
        return null;
    }

    @Override
    public ServiceHandler serviceHandler() {
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
    public void Accept(Channel channel) {

    }
}
