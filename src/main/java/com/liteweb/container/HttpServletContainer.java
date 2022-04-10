package com.liteweb.container;

import com.liteweb.context.LiteWebContext;
import com.liteweb.entity.*;
import com.liteweb.enums.HttpMethodName;
import com.liteweb.enums.HttpStatusCode;

/**
 * 处理http请求，执行对应的行为
 */
public class HttpServletContainer implements Container,ServletContainer{
    /**
     * 报文转发容器
     * @param request 请求报文
     * @param response 返回报文
     */
    @Override
    public void disposeMessage(WebServlet request, WebServlet response , WebFilter filter) {
        HttpServletRequest http_request = (HttpServletRequest) request;
        HttpServletResponse http_response = (HttpServletResponse) response;
        HttpFilter httpFilter=(HttpFilter) filter;
        WebServlet webServlet = LiteWebContext.getInstance().getServletRegister()
                .findByPath(http_request.getRequestURI());
        if(!(webServlet instanceof HttpServlet)){
            http_response.setCode(HttpStatusCode.NOTFOUND.getCode());
            return;
        }
        if(httpFilter!=null){
            httpFilter.doFilter(http_request,http_response);
        }
        if(httpFilter!=null&&httpFilter.release()){
            //拦截器继续执行
            HttpServlet httpServlet=(HttpServlet) webServlet;
            HttpMethodName me_name = HttpMethodName.getInstance(http_request.getMethod());
            if(me_name==null){
                http_response.setCode(HttpStatusCode.BAD.getCode());
                return;
            }
            httpServlet.init(http_request);
            try {
                switch (me_name){
                    case GET:
                        httpServlet.doGet(http_request,http_response);
                        break;
                    case PUT:
                        httpServlet.doPut(http_request,http_response);
                        break;
                    case POST:
                        httpServlet.doPost(http_request,http_response);
                        break;
                    case DELETE:
                        httpServlet.doDelete(http_request,http_response);
                        break;
                }
            }catch (Error|Exception e){
                e.printStackTrace();
                http_response.setCode(HttpStatusCode.ERROR.getCode());
            }
            httpServlet.destroy();
        }
    }
}
