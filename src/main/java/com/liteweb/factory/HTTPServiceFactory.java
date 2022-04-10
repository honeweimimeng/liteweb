package com.liteweb.factory;

import com.liteweb.constant.ServerConstant;
import com.liteweb.service.HttpService;
import com.liteweb.service.HttpsService;
import com.liteweb.service.SocketService;
import java.nio.channels.Channel;

public class HTTPServiceFactory extends ServiceFactory{
    private final boolean isSSL;
    private Channel channel;

    public HTTPServiceFactory(boolean isSSL){
        this.isSSL=isSSL;
        name= isSSL ? "Https服务":"Http服务";
    }

    @Override
    public SocketService production(Channel channel) {
        this.channel=channel;
        return isSSL ? productionSSL():productionDefault();
    }

    /**
     * 非SSL
     * @return SocketService
     */
    private SocketService productionDefault(){
        return new HttpService(channel);
    }

    /**
     * SSL
     * @return SocketService
     */
    private SocketService productionSSL(){
        return new HttpsService(channel);
    }
}
