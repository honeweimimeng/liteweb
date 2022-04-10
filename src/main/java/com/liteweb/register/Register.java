package com.liteweb.register;

import com.liteweb.entity.WebFilter;
import com.liteweb.entity.WebServlet;
import com.liteweb.register.inter.Registry;
import com.liteweb.service.SocketService;

public abstract class Register<T> implements Registry<T> {
    public Integer getServiceCount(){
        return service_map.size();
    }
    public SocketService findByProtocol(String path){
        return service_map.get(path);
    }
    public Integer getServletCount(){
        return servlet_map.size();
    }
    public WebServlet findByPath(String path){
        return servlet_map.get(path);
    }
    public WebFilter findByFilterPath(String path){
        //从最长路径搜索过滤器
        while (path.contains("/")){
            WebFilter webFilter = filter_map.get(path+"/*");
            if(webFilter!=null){
                return webFilter;
            }
            path=path.substring(0,path.lastIndexOf("/"));
        }
        return filter_map.get(path+"/*");
    }
}
