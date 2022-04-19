package com.liteweb.service;

import com.liteweb.builder.SocketIoBuilder;
import com.liteweb.connector.HttpServletConnector;
import com.liteweb.connector.ServletConnector;
import com.liteweb.container.ServletContainer;
import com.liteweb.context.LiteWebContext;
import com.liteweb.entity.HttpServletRequest;
import com.liteweb.entity.HttpServletResponse;

import java.io.IOException;
import java.nio.channels.*;
import java.util.List;

public abstract class SocketService extends BaseService{
    public abstract Channel getChannel();
    public abstract ServiceHandler serviceHandler(Object... objects);
    abstract ServletContainer servletContainer();
    abstract List<ServletConnector> servletConnectors();
    public SocketChannel Accept(boolean isBlocking, SelectionKey key, Selector selector) throws Exception {
        SocketChannel socketChannel=((ServerSocketChannel)key.channel()).accept();
        socketChannel.configureBlocking(isBlocking);
        socketChannel.register(selector, SelectionKey.OP_READ);
        return socketChannel;
    }
    public abstract Object Accept(Channel channel);
}