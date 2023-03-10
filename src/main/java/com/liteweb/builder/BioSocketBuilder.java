package com.liteweb.builder;

import com.liteweb.config.LiteWebConfig;
import com.liteweb.factory.LoggerFactory;
import com.liteweb.pool.ThreadPool;
import com.liteweb.scanner.SocketIoScanner;
import com.liteweb.service.SocketService;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

/**
 * @author Hone
 */
public class BioSocketBuilder extends SocketIoBuilder {
    private static final Logger logger= LoggerFactory.createInfo("BioSocket信息");
    @Override
    boolean isBlocking() {
        return true;
    }

    @Override
    boolean isAsync() {
        return false;
    }

    /**
     * 加载同步阻塞IO
     * @param socketService
     * socket通道
     */
    @Override
    public void loadingIoSocket(SocketService socketService) {
        ServerSocketChannel serverChannel =(ServerSocketChannel) getChannel();
        ThreadPool.SERVICE_POOL.execute(()->{
            logger.info("BIO通道加载，线程--->"+Thread.currentThread().getName()+"@ID"+Thread.currentThread().getId());
            new SocketIoScanner().startIoScanner(()->{
                SocketChannel socketChannel=serverChannel.accept();
                socketChannel.configureBlocking(isBlocking());
                //开启线程处理
                ThreadPool.SERVLET_POOL.execute(()->{
                    socketService.serviceHandler(socketService.accept(socketChannel)).invokeToInfo(socketChannel);
                });
            });
        });
    }
}
