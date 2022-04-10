package com.liteweb.config;

import com.liteweb.constant.ConfFileConstant;
import com.liteweb.constant.ServerConstant;
import com.liteweb.factory.LoggerFactory;
import com.liteweb.util.PropertiesFileUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Web基础配置
 */
public class LiteWebConfig extends ReadConfig{
    public static final Integer port;
    public static final String host;
    private static final LiteWebConfig runTimeConfig=new LiteWebConfig();
    public static final String ssl_protocol;
    public static final String ssl_certificateFile;
    public static final String ssl_certificatePwd;

    //初始化端口和host主机
    static {
        Integer[] int_res=runTimeConfig.loadConfInt(0,1,ConfFileConstant.OPERATION_PORT);
        String[] conf_str=runTimeConfig.loadConf(1,4,ConfFileConstant.SSL_PROTOCOL
                ,ConfFileConstant.SSL_FILE,ConfFileConstant.SSL_PASSWORD);
        port = int_res[0];
        ssl_protocol = conf_str[0];
        ssl_certificateFile = conf_str[1];
        ssl_certificatePwd = conf_str[2];
        String host1;
        try {
            host1 = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            host1 = ServerConstant.DEFAULT_HOST;
        }
        host = host1;
    }

    @Override
    String[] defaultValue() {
        return new String[]{ServerConstant.HTTP_PORT+"",ServerConstant.SSL_PROTOCOL
                ,ServerConstant.SSL_FILE,ServerConstant.SSL_PASSWORD};
    }
}