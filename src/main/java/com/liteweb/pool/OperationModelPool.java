package com.liteweb.pool;

import com.liteweb.builder.AioSocketBuilder;
import com.liteweb.builder.BioSocketBuilder;
import com.liteweb.builder.NioSocketBuilder;
import com.liteweb.builder.SocketIoBuilder;
import com.liteweb.constant.OperationConstant;
import com.liteweb.factory.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class OperationModelPool {
    private static final Map<String, Class<?>> OPERATION_MAP =new HashMap<>();
    private static final Logger logger= LoggerFactory.createException("模式错误");
    static {
        OPERATION_MAP.put(OperationConstant.BIO_RUN_MODEL,BioSocketBuilder.class);
        OPERATION_MAP.put(OperationConstant.NIO_RUN_MODEL,NioSocketBuilder.class);
        OPERATION_MAP.put(OperationConstant.AIO_RUN_MODEL,AioSocketBuilder.class);
    }
    public static SocketIoBuilder getBuilder(String name){
        try {
            Class<?> socketIoBuilder=OPERATION_MAP.get(name);
            if(socketIoBuilder==null){
                logger.severe("输入模式不支持，自动切换为AIO");
                return (SocketIoBuilder)OPERATION_MAP.get(OperationConstant.AIO_RUN_MODEL).newInstance();
            }
            return (SocketIoBuilder)socketIoBuilder.newInstance();
        }catch (Exception e){
            throw new RuntimeException("IO信道启动错误，建造者实例化错误");
        }
    }
}