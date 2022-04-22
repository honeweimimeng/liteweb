package com.liteweb.config;

import com.liteweb.constant.ConfFileConstant;
import com.liteweb.constant.RunTimeConstant;
import java.util.concurrent.TimeUnit;

/**
 * 服务运行时配置
 * @author Hone
 */
public class RunTimeConfig extends ReadConfig{
    /**
     * 线程池配置
     */
    public static final Integer SERVICE_CORE;
    public static final Integer SERVICE_MAX;
    public static final Integer SERVLET_CORE;
    public static final Integer SERVLET_MAX;
    public static final Integer SERVICE_KEEP_ALIVE;
    public static final Integer SERVLET_KEEP_ALIVE;
    public static final Integer SERVLET_HEADER_LENGTH;
    public static final TimeUnit KEEP_ALIVE_TIME_UTIL =RunTimeConstant.TIME_UNIT;
    private static final RunTimeConfig RUN_TIME_CONFIG =new RunTimeConfig();
    static {
        SERVICE_CORE=RUN_TIME_CONFIG.loadConfInt(ConfFileConstant.SERVICE_MAX_THREADS,0);
        SERVICE_MAX=RUN_TIME_CONFIG.loadConfInt(ConfFileConstant.SERVLET_CORE_THREADS,1);
        SERVLET_CORE=RUN_TIME_CONFIG.loadConfInt(ConfFileConstant.SERVLET_MAX_THREADS,2);
        SERVLET_MAX=RUN_TIME_CONFIG.loadConfInt(ConfFileConstant.SERVICE_KEEPALIVE_TIME,3);
        SERVICE_KEEP_ALIVE=RUN_TIME_CONFIG.loadConfInt(ConfFileConstant.SERVLET_KEEPALIVE_TIME,4);
        SERVLET_KEEP_ALIVE=RUN_TIME_CONFIG.loadConfInt(ConfFileConstant.SERVLET_HEADER_LENGTH,5);
        SERVLET_HEADER_LENGTH=RUN_TIME_CONFIG.loadConfInt(ConfFileConstant.SERVICE_CORE_THREADS,6);
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