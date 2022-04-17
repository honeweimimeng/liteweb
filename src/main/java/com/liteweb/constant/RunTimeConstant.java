package com.liteweb.constant;

import java.util.concurrent.TimeUnit;

/**
 * 运行时服务常量
 */
public class RunTimeConstant {
    //默认服务线程池大小配置
    public static final Integer SERVICE_POOL_MAX=10;
    public static final Integer SERVICE_POOL_CORE=5;
    //默认Servlet服务线程池大小配置
    public static final Integer SERVLET_POOL_CORE=20;
    public static final Integer SERVLET_POOL_MAX=50;
    //线程keepAlive时间
    public static final Integer SERVICE_KEEPALIVE=30;
    public static final Integer SERVLET_KEEPALIVE=10;
    public static final TimeUnit TIME_UNIT=TimeUnit.MINUTES;
    //请求头最大长度,8K
    public static final Integer MAX_HEADER_LENGTH=8192;
    public static final String NULL_STR=" ";
    public static final String SESSION_KEY="LITE_WEB_SESSION";
}
