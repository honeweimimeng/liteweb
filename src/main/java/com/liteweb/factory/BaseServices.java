package com.liteweb.factory;

import com.liteweb.service.BaseService;
import java.net.InetSocketAddress;

public class BaseServices {
    private final String hostName;

    public BaseServices(String hostName){
        this.hostName=hostName;
    }
    /**
     * Http原型
     * @return 服务原型
     */
    public BaseService createHttp(Integer port){
        return new BaseService(new HTTPServiceFactory(false),new InetSocketAddress(hostName,port),"Http") {
            @Override
            public String getName() {
                return super.getName();
            }
        };
    }

    /**
     * Https原型
     * @return 服务原型
     */
    public BaseService createHttps(Integer port){
        return new BaseService(new HTTPServiceFactory(true),new InetSocketAddress(hostName,port),"Https") {
            @Override
            public String getName() {
                return super.getName();
            }
        };
    }
}
