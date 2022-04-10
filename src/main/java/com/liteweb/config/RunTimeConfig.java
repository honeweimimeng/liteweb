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
        Integer[] values=runTimeConfig.loadConfInt(0,7,ConfFileConstant.SERVICE_CORE_THREADS
                ,ConfFileConstant.SERVICE_MAX_THREADS,ConfFileConstant.SERVLET_CORE_THREADS
                ,ConfFileConstant.SERVLET_MAX_THREADS,ConfFileConstant.SERVICE_KEEPALIVE_TIME
                ,ConfFileConstant.SERVLET_KEEPALIVE_TIME,ConfFileConstant.SERVLET_HEADER_LENGTH);
        service_core=values[0];
        service_max=values[1];
        servlet_core=values[2];
        servlet_max=values[3];
        service_keepAlive=values[4];
        servlet_keepAlive=values[5];
        servlet_header_length=values[6];
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