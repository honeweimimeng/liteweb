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
 * @author Hone
 */
public class LiteWebConfig extends ReadConfig{
    public static final Integer HTTP_PORT;
    public static final Integer HTTPS_PORT;
    public static final String HOST;
    private static final LiteWebConfig WEB_CONFIG =new LiteWebConfig();
    public static final String SSL_PROTOCOL;
    public static final String SSL_CERTIFICATE_FILE;
    public static final String SSL_CERTIFICATE_PWD;
    public static final String SSL_CERTIFICATE_FILE_TRUST;
    public static final String SSL_CERTIFICATE_PWD_TRUST;

    //初始化端口和host主机
    static {
        HTTP_PORT=WEB_CONFIG.loadConfInt(ConfFileConstant.OPERATION_HTTP_PORT,0);
        HTTPS_PORT=WEB_CONFIG.loadConfInt(ConfFileConstant.OPERATION_HTTPS_PORT,1);
        SSL_PROTOCOL=WEB_CONFIG.loadConf(ConfFileConstant.SSL_PROTOCOL,2);
        SSL_CERTIFICATE_FILE=WEB_CONFIG.loadConf(ConfFileConstant.SSL_FILE,3);
        SSL_CERTIFICATE_PWD=WEB_CONFIG.loadConf(ConfFileConstant.SSL_PASSWORD,4);
        SSL_CERTIFICATE_FILE_TRUST=WEB_CONFIG.loadConf(ConfFileConstant.SSL_FILE_TRUST,5);
        SSL_CERTIFICATE_PWD_TRUST=WEB_CONFIG.loadConf(ConfFileConstant.SSL_PASSWORD_TRUST,6);
        String host1;
        try {
            host1 = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            host1 = ServerConstant.DEFAULT_HOST;
        }
        HOST = host1;
    }

    @Override
    String[] defaultValue() {
        return new String[]{ServerConstant.HTTP_PORT+"",ServerConstant.HTTPS_PORT+"",ServerConstant.SSL_PROTOCOL
                ,ServerConstant.SSL_FILE,ServerConstant.SSL_PASSWORD,ServerConstant.SSL_FILE_TRUST
                ,ServerConstant.SSL_PASSWORD_TRUST};
    }
}