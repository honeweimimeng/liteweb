package com.liteweb.register;

import com.liteweb.entity.WebServlet;

public class ServletRegister<T> extends Register<T> {
    @Override
    public void Register(String key,T t) {
        if(t instanceof WebServlet){
            servlet_map.put(key,(WebServlet) t);
        }
    }

    @Override
    public void Cancellation(String key) {
        servlet_map.remove(key);
    }
}
