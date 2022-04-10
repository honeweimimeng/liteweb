package com.liteweb.pool;

import com.liteweb.config.RunTimeConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池工厂
 */
public class ThreadPool {

    /**
     * 服务线程池工厂方法
     */
    public static final ExecutorService servicePool=new ThreadPoolExecutor(RunTimeConfig.service_core,
            RunTimeConfig.service_max,RunTimeConfig.service_keepAlive,
            RunTimeConfig.keepAlive_timeUtil,new LinkedBlockingQueue<>());

    /**
     * Servlet程池工厂方法
     */
    public static final ExecutorService servletPool=new ThreadPoolExecutor(RunTimeConfig.servlet_core,
            RunTimeConfig.servlet_max,RunTimeConfig.servlet_keepAlive,
            RunTimeConfig.keepAlive_timeUtil,new LinkedBlockingQueue<>());
}
