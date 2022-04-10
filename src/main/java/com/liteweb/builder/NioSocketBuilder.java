package com.liteweb.builder;

import com.liteweb.config.LiteWebConfig;
import com.liteweb.exception.LiteWebException;
import com.liteweb.factory.LoggerFactory;
import com.liteweb.pool.ThreadPool;
import com.liteweb.scanner.SocketIoScanner;
import com.liteweb.service.ServiceHandler;
import com.liteweb.service.SocketService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.logging.Logger;

public class NioSocketBuilder extends SocketIoBuilder {
    private static final Logger logger= LoggerFactory.createInfo("NioSocket信息");
    private Selector selector;
    @Override
    boolean isBlocking() {
        return false;
    }

    @Override
    boolean isAsync() {
        return false;
    }

    /**
     * 非阻塞IO结合选择器,调用服务accept事件和读取事件
     * @param socketService socket服务
     */
    @Override
    public void loadingIoSocket(SocketService socketService) {
        ServerSocketChannel server_channel=(ServerSocketChannel) getChannel();
        ThreadPool.servicePool.execute(()->{
            logger.info("NIO通道加载，线程--->"+Thread.currentThread().getName()+"@ID"+Thread.currentThread().getId());
            try {
                selector=Selector.open();
                server_channel.register(selector, SelectionKey.OP_ACCEPT);
            } catch (IOException e) {
                e.printStackTrace();
                throw new LiteWebException("NIO轮询器开启失败"+e.getMessage());
            }
            new SocketIoScanner().startIoScanner(()->{
                selector.select();
                Iterator<SelectionKey> iterator=selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey key=iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        socketService.Accept(isBlocking(),key,selector);
                    }else if(key.isReadable()){
                        SocketChannel socketChannel=(SocketChannel) key.channel();
                        //关闭当前key的关心操作
                        key.interestOps(0);
                        //开启线程处理
                        ThreadPool.servletPool.execute(()->{
                            socketService.serviceHandler().invokeToInfo(socketChannel);
                        });
                    }
                }
            });
        });
    }
}
