package com.liteweb.register.inter;

import com.liteweb.entity.HttpServlet;
import com.liteweb.entity.WebFilter;
import com.liteweb.entity.WebServlet;
import com.liteweb.service.SocketService;

import java.util.HashMap;
import java.util.Map;

public interface Registry<T> {
    Map<String, SocketService> service_map=new HashMap<>();
    Map<String, WebServlet> servlet_map=new HashMap<>();
    Map<String, WebFilter> filter_map=new HashMap<>();
    void Register(String key,T t);
    void Cancellation(String key);
}
