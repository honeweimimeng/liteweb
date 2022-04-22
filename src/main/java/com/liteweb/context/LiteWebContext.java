package com.liteweb.context;

import com.liteweb.entity.WebFilter;
import com.liteweb.entity.WebServlet;
import com.liteweb.exception.ServerException;
import com.liteweb.register.FilterRegister;
import com.liteweb.register.ServiceRegister;
import com.liteweb.register.ServletRegister;
import com.liteweb.service.BaseService;
import com.liteweb.service.SocketService;

/**
 * @author Hone
 */
public class LiteWebContext {
    private volatile static LiteWebContext liteWebContext;
    private final ServiceRegister<BaseService> serviceRegister= new ServiceRegister<>();
    private final ServletRegister<WebServlet> servletRegister=new ServletRegister<>();
    private final FilterRegister<WebFilter> filterRegister=new FilterRegister<>();
    private LiteWebContext(){
        if(liteWebContext!=null){
            throw new ServerException("上下文已经存在");
        }
    }
    public static LiteWebContext getInstance(){
        if(liteWebContext==null){
            synchronized (LiteWebContext.class){
                if(liteWebContext==null){
                    liteWebContext = new LiteWebContext();
                }
            }
        }
        return liteWebContext;
    }

    public ServiceRegister<BaseService> getServiceRegister() {
        return serviceRegister;
    }

    public ServletRegister<WebServlet> getServletRegister() {
        return servletRegister;
    }

    public FilterRegister<WebFilter> getFilterRegister() {
        return filterRegister;
    }
}
