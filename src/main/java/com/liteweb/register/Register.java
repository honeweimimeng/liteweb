package com.liteweb.register;

import com.liteweb.entity.WebFilter;
import com.liteweb.entity.WebServlet;
import com.liteweb.register.inter.Registry;
import com.liteweb.service.SocketService;

/**
 * @author Hone
 */
public abstract class Register<T> implements Registry<T> {
    public Integer getServiceCount(){
        return SERVICE_MAP.size();
    }
    public SocketService findByProtocol(String path){
        return SERVICE_MAP.get(path);
    }
    public Integer getServletCount(){
        return SERVLET_MAP.size();
    }
    public WebServlet findByPath(String path){
        return SERVLET_MAP.get(path);
    }
    public WebFilter findByFilterPath(String path){
        if(path==null){
            return null;
        }
        //从最长路径搜索过滤器
        while (path.contains("/")){
            WebFilter webFilter = FILTER_MAP.get(path+"/*");
            if(webFilter!=null){
                return webFilter;
            }
            path=path.substring(0,path.lastIndexOf("/"));
        }
        return FILTER_MAP.get(path+"/*");
    }
}
