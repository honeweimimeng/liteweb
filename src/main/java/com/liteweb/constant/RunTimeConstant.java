package com.liteweb.constant;

import java.util.concurrent.TimeUnit;

/**
 * 运行时服务常量
 * @author Hone
 */
public class RunTimeConstant {
    public static final Integer SERVICE_POOL_MAX=10;
    public static final Integer SERVICE_POOL_CORE=5;
    public static final Integer SERVLET_POOL_CORE=20;
    public static final Integer SERVLET_POOL_MAX=50;
    public static final Integer SERVICE_KEEPALIVE=30;
    public static final Integer SERVLET_KEEPALIVE=10;
    public static final TimeUnit TIME_UNIT=TimeUnit.MINUTES;
    public static final Integer MAX_HEADER_LENGTH=8192;
    public static final String NULL_STR=" ";
    public static final String SESSION_KEY="LITE_WEB_SESSION";
    public static final String CONF_IS_START="true";
}
