package com.liteweb.service;

import com.liteweb.factory.ServiceFactory;
import java.net.InetSocketAddress;

public abstract class BaseService {
    private ServiceFactory serviceFactory;
    private InetSocketAddress socketAddress;
    private String name;

    public BaseService(){

    }
    public BaseService(ServiceFactory serviceFactory,InetSocketAddress socketAddress,String name){
        this.serviceFactory=serviceFactory;
        this.socketAddress=socketAddress;
        this.name=name;
    }

    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }

    public ServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    public String getName() {
        return name;
    }
}
