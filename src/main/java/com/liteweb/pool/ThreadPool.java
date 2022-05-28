package com.liteweb.pool;

import com.liteweb.config.RunTimeConfig;
import com.liteweb.factory.ThreadPoolFactory;

import java.util.concurrent.*;

/**
 * 线程池工厂
 * @author Hone
 */
public class ThreadPool {

    /**
     * 服务线程池工厂方法
     */
    public static final ExecutorService SERVICE_POOL =new ThreadPoolExecutor(RunTimeConfig.SERVICE_CORE,
            RunTimeConfig.SERVICE_MAX,RunTimeConfig.SERVICE_KEEP_ALIVE,
            RunTimeConfig.KEEP_ALIVE_TIME_UTIL,new LinkedBlockingQueue<>(),new ThreadPoolFactory("服务线程"));

    /**
     * Servlet程池工厂方法
     */
    public static final ExecutorService SERVLET_POOL =new ThreadPoolExecutor(RunTimeConfig.SERVLET_CORE,
            RunTimeConfig.SERVLET_MAX,RunTimeConfig.SERVLET_KEEP_ALIVE,
            RunTimeConfig.KEEP_ALIVE_TIME_UTIL,new LinkedBlockingQueue<>(),new ThreadPoolFactory("Servlet线程"));

    /**
     * 延时线程池，用于热加载扫描
     */
    public static final ScheduledExecutorService HOTSPOT_POOL = Executors.newScheduledThreadPool(1);
}
