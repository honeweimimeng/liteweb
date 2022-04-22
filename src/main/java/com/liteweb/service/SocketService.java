package com.liteweb.service;

import com.liteweb.connector.ServletConnector;
import com.liteweb.container.ServletContainer;

import java.nio.channels.*;
import java.util.List;

/**
 * @author Hone
 */
public abstract class SocketService extends BaseService{

    /**
     * 通信管道
     * @return Channel
     */
    public abstract Channel getChannel();

    /**
     * 获取服务操作器实例
     * @param objects 操作参数
     * @return ServiceHandler
     */
    public abstract ServiceHandler serviceHandler(Object... objects);

    /**
     * 服务对应的Servlet容器
     * @return ServletContainer
     */
    abstract ServletContainer servletContainer();

    /**
     * Servlet连接列表
     * @return List<ServletConnector>
     */
    abstract List<ServletConnector> servletConnectors();

    /**
     * 带选择器的接受连接操作，通常为初始化操作
     * @param isBlocking 管道阻塞状态
     * @param key 选择键
     * @param selector 选择器
     * @throws Exception 配置异常
     */
    public void accept(boolean isBlocking, SelectionKey key, Selector selector) throws Exception {
        SocketChannel socketChannel=((ServerSocketChannel)key.channel()).accept();
        socketChannel.configureBlocking(isBlocking);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    /**
     * 接受连接操作，通常为初始化操作
     * @param channel 通信管道
     * @return 初始化返回的对象附加参数
     */
    public abstract Object accept(Channel channel);
}