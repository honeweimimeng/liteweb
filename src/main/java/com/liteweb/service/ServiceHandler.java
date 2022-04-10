package com.liteweb.service;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;

public interface ServiceHandler {
    void invokeToInfo(Channel channel);
}
