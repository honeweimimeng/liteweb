package com.liteweb.connector;

import java.nio.channels.Channel;

public abstract class ServletConnector {
    public ServletConnector(Channel socketChannel){
        this.socketChannel=socketChannel;
    }
    //Socket通信管道
    protected Channel socketChannel;

    public Channel getSocketChannel() {
        return socketChannel;
    }
}
