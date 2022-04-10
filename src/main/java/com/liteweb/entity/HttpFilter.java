package com.liteweb.entity;

public abstract class HttpFilter implements WebFilter{
    public abstract void doFilter(HttpServletRequest request,HttpServletResponse response);
    public abstract void destroy();
    public abstract boolean release();
}