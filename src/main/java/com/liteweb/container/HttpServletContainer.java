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
 * @author Hone
 */
public class HttpServletContainer extends ServletContainer{
    /**
     * 报文转发容器
     * @param request 请求报文
     * @param response 返回报文
     */
    @Override
    public void disposeMessage(WebServlet request, WebServlet response , WebFilter filter) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpFilter httpFilter=(HttpFilter) filter;
        String requestUrl = httpRequest.getRequestUri();
        if(requestUrl==null){
            return;
        }
        if(requestUrl.contains(".")){
            try {
                String lastName=requestUrl.substring(requestUrl.lastIndexOf(".")+1);
                httpResponse.setOutputStream(ResponseUtil.responseResourceFile(requestUrl));
                httpResponse.setContentType(ResponseUtil.getFileContentType(lastName));
                if(ResponseUtil.isDownLoadFile(lastName)){
                    String fileName=requestUrl.substring(requestUrl.lastIndexOf("/")+1);
                    httpResponse.addHeader("Content-Disposition","attachement;filename=" + fileName);
                }
            }catch (Exception e){
                httpResponse.setCode(HttpStatusCode.NOTFOUND.getCode());
            }
            return;
        }
        WebServlet webServlet = LiteWebContext.getInstance().getServletRegister()
                .findByPath(httpRequest.getRequestUri());
        if(!(webServlet instanceof HttpServlet)){
            httpResponse.setCode(HttpStatusCode.NOTFOUND.getCode());
            return;
        }
        if(httpFilter!=null){
            httpFilter.doFilter(httpRequest,httpResponse);
        }
        if(httpFilter!=null&&httpFilter.release()){
            HttpServlet httpServlet = (HttpServlet) webServlet;
            HttpMethodName meName = HttpMethodName.getInstance(httpRequest.getMethod());
            CookieContext cookieContext = httpRequest.getCookieContext();
            ServerSession serverSession = disposeSession(httpServlet,cookieContext==null ? new Cookie():
                    cookieContext.getCookie(RunTimeConstant.SESSION_KEY));
            if(serverSession!=null){
                SimpleDateFormat format=new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
                httpResponse.addHeader(HttpConstant.HEADER_SET_COOKIE,RunTimeConstant.SESSION_KEY+
                        ServerConstant.END_STR +serverSession.getSessionId()+HttpConstant.HEADER_COOKIE_EXPIRES+
                        format.format(serverSession.getTimeOut()));
            }
            realDispose(httpServlet,meName,httpRequest,httpResponse);
        }
    }

    /**
     * 直接执行
     * @param httpServlet 目标实体
     * @param meName HTTP方法名称
     * @param request 请求报文
     * @param response 响应报文
     */
    public void realDispose(HttpServlet httpServlet, HttpMethodName meName, HttpServletRequest request, HttpServletResponse response){
        //拦截器继续执行
        if(meName==null){
            response.setCode(HttpStatusCode.BAD.getCode());
            return;
        }
        httpServlet.init(request);
        try {
            String nameAim =null;
            switch (meName){
                case GET:
                    nameAim="doGet";
                    break;
                case PUT:
                    nameAim="doPut";
                    break;
                case POST:
                    nameAim="doPost";
                    break;
                case DELETE:
                    nameAim="doDelete";
                    break;
                default:
                    response.setCode(HttpStatusCode.NOT_ALLOWED.getCode());
                    break;
            }
            if(nameAim!=null){
                doMethod(httpServlet,nameAim,request,response,HttpServletRequest.class,HttpServletResponse.class);
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
            String str=ResponseUtil.byteArrayToString(request.getBodyStream());
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
