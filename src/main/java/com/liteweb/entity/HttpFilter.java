package com.liteweb.entity;

/**
 * @author Hone
 */
public abstract class HttpFilter implements WebFilter{

    /**
     * 执行拦截器
     * @param request 请求报文
     * @param response 响应报文
     */
    public abstract void doFilter(HttpServletRequest request,HttpServletResponse response);

    /**
     * 前置操作
     */
    public abstract void destroy();

    /**
     * 是否拦截
     * @return boolean
     */
    public abstract boolean release();
}