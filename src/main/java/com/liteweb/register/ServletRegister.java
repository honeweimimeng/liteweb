package com.liteweb.register;

import com.liteweb.entity.WebServlet;

/**
 * @author Hone
 */
public class ServletRegister<T> extends Register<T> {
    @Override
    public void register(String key, T t) {
        if(t instanceof WebServlet){
            SERVLET_MAP.put(key,(WebServlet) t);
        }
    }

    @Override
    public void cancellation(String key) {
        SERVLET_MAP.remove(key);
    }
}
