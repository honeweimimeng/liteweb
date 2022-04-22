package com.liteweb.builder;

import com.liteweb.context.LiteWebContext;
import com.liteweb.factory.LoggerFactory;
import com.liteweb.factory.ServiceFactory;
import com.liteweb.service.SocketService;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

/**
 * 建造者组装部件
 * @author Hone
 */
public class SocketIoDirector {
    private static final Logger logger = LoggerFactory.createInfo("Service组建信息");
    private final SocketIoBuilder socketIoBuilder;
    private final ServiceFactory serviceFactory;
    private final InetSocketAddress inetSocketAddress;
    public SocketIoDirector(SocketIoBuilder socketIoBuilder, ServiceFactory serviceFactory, InetSocketAddress inetSocketAddress){
        this.socketIoBuilder=socketIoBuilder;
        this.serviceFactory=serviceFactory;
        this.inetSocketAddress=inetSocketAddress;
    }
    /**
     * 组织建造者,生产SocketService
     * 建造器
     * @return SocketService
     */
    public SocketService create(){
        logger.info(serviceFactory.name+"服务开始组建--->");
        //开始服务引导->创建通信管道->服务被引导完成
        SocketService socketService=serviceFactory.production(socketIoBuilder.buildChannel(inetSocketAddress));
        //加载通信管道
        socketIoBuilder.loadingIoSocket(socketService);
        logger.info(serviceFactory.name+"服务组建结束--->"+ "端口："+ inetSocketAddress.getPort());
        return socketService;
    }
}