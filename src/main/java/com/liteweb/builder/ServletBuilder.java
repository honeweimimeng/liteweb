package com.liteweb.builder;

import com.liteweb.entity.WebServlet;

import java.nio.channels.Channel;

/**
 * @author Hone
 */
public abstract class ServletBuilder {
    protected final Channel channel;
    public ServletBuilder(Channel channel){
        this.channel=channel;
    }
    /**
     * 请求Servlet实例
     * @return HttpServlet
     */
    public abstract WebServlet buildRequest();

    /**
     * 返回Servlet实例
     * @return HttpServlet
     */
    public abstract WebServlet buildResponse();
}
