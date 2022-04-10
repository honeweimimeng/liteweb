package com.liteweb.scanner;

import com.liteweb.builder.SocketIoBuilder;
import com.liteweb.builder.SocketIoDirector;
import com.liteweb.pool.OperationModelPool;
import com.liteweb.service.BaseService;
import com.liteweb.service.SocketService;
import com.liteweb.util.PropertiesFileUtil;

/**
 * Socket服务扫描器
 */
public class SocketServiceServiceScanner extends ServiceScanner{

    /**
     * 组装服务，注册服务
     * @param service 基础服务
     */
    @Override
    public void Deploy(BaseService... service) {
        for (BaseService baseService : service) {
            SocketIoBuilder socketIoBuilder = OperationModelPool.getBuilder(PropertiesFileUtil.getOperationModel());
            SocketService socketService = new SocketIoDirector(socketIoBuilder,baseService.getServiceFactory(),baseService.getSocketAddress()).create();
            register(baseService.getName(),socketService);
        }
    }
}
