package com.liteweb.builder;

import com.liteweb.entity.WebServlet;

public abstract class ServletBuilder {
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
