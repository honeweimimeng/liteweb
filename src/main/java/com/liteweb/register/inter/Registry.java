package com.liteweb.register.inter;

import com.liteweb.entity.HttpServlet;
import com.liteweb.entity.WebFilter;
import com.liteweb.entity.WebServlet;
import com.liteweb.service.SocketService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Hone
 */
public interface Registry<T> {
    Map<String, SocketService> SERVICE_MAP =new ConcurrentHashMap<>();
    Map<String, WebServlet> SERVLET_MAP =new ConcurrentHashMap<>();
    Map<String, WebFilter> FILTER_MAP =new ConcurrentHashMap<>();

    /**
     * 注册到相应的实体注册器
     * @param key 注册器键
     * @param t 注册实体
     */
    void register(String key, T t);

    /**
     * 取消注册
     * @param key 注册键
     */
    void cancellation(String key);
}
