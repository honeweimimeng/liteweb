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
import java.nio.channels.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class AioSocketBuilder extends SocketIoBuilder {
    private static final Logger logger= LoggerFactory.createInfo("AioSocket信息");
    @Override
    boolean isBlocking() {
        return false;
    }

    @Override
    boolean isAsync() {
        return true;
    }

    /**
     * 异步IO使用socket服务，执行serviceHandler,获取回调，继续监听
     * @param socketService 服务
     */
    @Override
    public void loadingIoSocket(SocketService socketService) {
        AsynchronousServerSocketChannel server_channel=(AsynchronousServerSocketChannel) getChannel();
        ThreadPool.servicePool.execute(()->{
            logger.info("AIO通道加载，线程--->"+Thread.currentThread().getName()+"@ID"+Thread.currentThread().getId());
            server_channel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                @Override
                public void completed(AsynchronousSocketChannel result, Object attachment) {
                    server_channel.accept(null,this);
                    //开启线程处理
                    ThreadPool.servletPool.execute(()->{
                        socketService.serviceHandler(socketService.Accept(result)).invokeToInfo(result);
                    });
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    exc.printStackTrace();
                    throw new LiteWebException("AIOSocket开启失败"+exc.getMessage());
                }
            });
            new SocketIoScanner().startIoScanner();
        });
    }
}