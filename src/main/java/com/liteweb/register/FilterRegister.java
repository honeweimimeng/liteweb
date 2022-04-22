package com.liteweb.register;

import com.liteweb.entity.WebFilter;

/**
 * @author Hone
 */
public class FilterRegister<T> extends Register<T>{
    @Override
    public void register(String key, T t) {
        if(t instanceof WebFilter){
            FILTER_MAP.put(key,(WebFilter) t);
        }
    }

    @Override
    public void cancellation(String key) {
        FILTER_MAP.remove(key);
    }
}
