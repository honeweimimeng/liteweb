package com.liteweb.container;

import com.alibaba.fastjson.JSONObject;
import com.liteweb.anno.EntityParameter;
import com.liteweb.anno.JsonRest;
import com.liteweb.anno.StaticPageRest;
import com.liteweb.constant.HttpConstant;
import com.liteweb.constant.RunTimeConstant;
import com.liteweb.constant.ServerConstant;
import com.liteweb.context.LiteWebContext;
import com.liteweb.entity.*;
import com.liteweb.enums.HttpMethodName;
import com.liteweb.enums.HttpStatusCode;
import com.liteweb.util.ResponseUtil;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 处理http请求，执行对应的行为
 */
public class HttpServletContainer extends ServletContainer{
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
        String requestURL=http_request.getRequestURI();
        if(requestURL==null){
            return;
        }
        if(requestURL.contains(".")){
            try {
                String lastName=requestURL.substring(requestURL.lastIndexOf(".")+1);
                http_response.setOutputStream(ResponseUtil.responseResourceFile(requestURL));
                http_response.setContentType(ResponseUtil.getFileContentType(lastName));
                if(ResponseUtil.isDownLoadFile(lastName)){
                    String fileName=requestURL.substring(requestURL.lastIndexOf("/")+1);
                    http_response.addHeader("Content-Disposition","attachement;filename=" + fileName);
                }
            }catch (Exception e){
                http_response.setCode(HttpStatusCode.NOTFOUND.getCode());
            }
            return;
        }
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
            HttpServlet httpServlet = (HttpServlet) webServlet;
            HttpMethodName me_name = HttpMethodName.getInstance(http_request.getMethod());
            CookieContext cookieContext = http_request.getCookieContext();
            ServerSession serverSession = disposeSession(httpServlet,cookieContext==null ? new Cookie():
                    cookieContext.getCookie(RunTimeConstant.SESSION_KEY));
            if(serverSession!=null){
                SimpleDateFormat format=new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
                http_response.addHeader(HttpConstant.HEADER_SET_COOKIE,RunTimeConstant.SESSION_KEY+
                        ServerConstant.EqualsStr +serverSession.getSessionID()+HttpConstant.HEADER_COOKIE_EXPIRES+
                        format.format(serverSession.getTimeOut()));
            }
            realDispose(httpServlet,me_name,http_request,http_response);
        }
    }

    /**
     * 直接执行
     * @param httpServlet 目标实体
     * @param me_name HTTP方法名称
     * @param request 请求报文
     * @param response 响应报文
     */
    public void realDispose(HttpServlet httpServlet,HttpMethodName me_name,HttpServletRequest request,HttpServletResponse response){
        //拦截器继续执行
        if(me_name==null){
            response.setCode(HttpStatusCode.BAD.getCode());
            return;
        }
        httpServlet.init(request);
        try {
            String name_aim=null;
            switch (me_name){
                case GET:
                    name_aim="doGet";
                    break;
                case PUT:
                    name_aim="doPut";
                    break;
                case POST:
                    name_aim="doPost";
                    break;
                case DELETE:
                    name_aim="doDelete";
                    break;
            }
            if(name_aim!=null){
                doMethod(httpServlet,name_aim,request,response,HttpServletRequest.class,HttpServletResponse.class);
            }
        }catch (Error|Exception e){
            e.printStackTrace();
            response.setCode(HttpStatusCode.ERROR.getCode());
        }
        httpServlet.destroy();
    }

    /**
     * 执行方法，处理注解
     * @param httpServlet 目标实体
     * @param name 方法名称
     * @param request 请求报文
     * @param response 响应报文
     * @param parameterTypes 方法类型
     * @throws NoSuchMethodException 找不到方法
     * @throws InvocationTargetException,IllegalAccessException 方法执行异常
     * @throws NoSuchMethodException,IOException 参数注入实例化异常
     */
    public static void doMethod(HttpServlet httpServlet,String name,HttpServletRequest request
            ,HttpServletResponse response,Class<?>... parameterTypes)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
            , InstantiationException, IOException {
        Class<?> clazz=httpServlet.getClass();
        Method method=clazz.getMethod(name, parameterTypes);
        //请求报文设置请求实体，修改实体
        Parameter parameter = method.getParameters()[0];
        EntityParameter entityParameter=parameter.getAnnotation(EntityParameter.class);
        if(entityParameter!=null){
            String str=ResponseUtil.ByteArrayToString(request.getBodyStream());
            request.setBodyEntity(JSONObject.parseObject(str,entityParameter.clazz()));
        }
        method.invoke(httpServlet, request,response);
        //更改响应报文
        StaticPageRest staticPageRest=method.getAnnotation(StaticPageRest.class);
        if(staticPageRest!=null){
            response.setContentType(staticPageRest.type());
            response.setOutputStream(ResponseUtil.responseResourceFile(staticPageRest.path()));
        }
        JsonRest jsonRest=method.getAnnotation(JsonRest.class);
        if(jsonRest!=null){
            response.setContentType(jsonRest.type());
            response.setOutputStream(ResponseUtil.responseResourceJson(response.getBodyObject()));
        }
    }
}
