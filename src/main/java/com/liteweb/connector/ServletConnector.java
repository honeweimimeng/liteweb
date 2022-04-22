package com.liteweb.connector;

import java.nio.channels.Channel;

/**
 * @author Hone
 */
public abstract class ServletConnector {
    public ServletConnector(Channel socketChannel){
        this.socketChannel=socketChannel;
    }

    protected Channel socketChannel;

    public Channel getSocketChannel() {
        return socketChannel;
    }
}
