package com.liteweb.config;

import com.liteweb.constant.ConfFileConstant;
import com.liteweb.constant.RunTimeConstant;
import java.util.concurrent.TimeUnit;

/**
 * 服务运行时配置
 */
public class RunTimeConfig extends ReadConfig{
    /**
     * 线程池配置
     */
    public static final Integer service_core;
    public static final Integer service_max;
    public static final Integer servlet_core;
    public static final Integer servlet_max;
    public static final Integer service_keepAlive;
    public static final Integer servlet_keepAlive;
    public static final Integer servlet_header_length;
    public static final TimeUnit keepAlive_timeUtil=RunTimeConstant.TIME_UNIT;
    private static final RunTimeConfig runTimeConfig=new RunTimeConfig();
    static {
        service_core=runTimeConfig.loadConfInt(ConfFileConstant.SERVICE_MAX_THREADS,0);
        service_max=runTimeConfig.loadConfInt(ConfFileConstant.SERVLET_CORE_THREADS,1);
        servlet_core=runTimeConfig.loadConfInt(ConfFileConstant.SERVLET_MAX_THREADS,2);
        servlet_max=runTimeConfig.loadConfInt(ConfFileConstant.SERVICE_KEEPALIVE_TIME,3);
        service_keepAlive=runTimeConfig.loadConfInt(ConfFileConstant.SERVLET_KEEPALIVE_TIME,4);
        servlet_keepAlive=runTimeConfig.loadConfInt(ConfFileConstant.SERVLET_HEADER_LENGTH,5);
        servlet_header_length=runTimeConfig.loadConfInt(ConfFileConstant.SERVICE_CORE_THREADS,6);
    }

    @Override
    String[] defaultValue() {
        return new String[]{RunTimeConstant.SERVICE_POOL_CORE.toString(),
                RunTimeConstant.SERVICE_POOL_MAX.toString(),
                RunTimeConstant.SERVLET_POOL_CORE.toString(),
                RunTimeConstant.SERVLET_POOL_MAX.toString(),
                RunTimeConstant.SERVICE_KEEPALIVE.toString(),
                RunTimeConstant.SERVLET_KEEPALIVE.toString(),
                RunTimeConstant.MAX_HEADER_LENGTH.toString()
        };
    }
}