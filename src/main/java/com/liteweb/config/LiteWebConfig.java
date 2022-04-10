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
    public static final Integer http_port;
    public static final Integer https_port;
    public static final String host;
    private static final LiteWebConfig webConfig=new LiteWebConfig();
    public static final String ssl_protocol;
    public static final String ssl_certificateFile;
    public static final String ssl_certificatePwd;

    //初始化端口和host主机
    static {
        http_port=webConfig.loadConfInt(ConfFileConstant.OPERATION_HTTP_PORT,0);
        https_port=webConfig.loadConfInt(ConfFileConstant.OPERATION_HTTPS_PORT,1);
        ssl_protocol=webConfig.loadConf(ConfFileConstant.SSL_PROTOCOL,2);
        ssl_certificateFile=webConfig.loadConf(ConfFileConstant.SSL_FILE,3);
        ssl_certificatePwd=webConfig.loadConf(ConfFileConstant.SSL_PASSWORD,4);
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
        return new String[]{ServerConstant.HTTP_PORT+"",ServerConstant.HTTPS_PORT+"",ServerConstant.SSL_PROTOCOL
                ,ServerConstant.SSL_FILE,ServerConstant.SSL_PASSWORD};
    }
}