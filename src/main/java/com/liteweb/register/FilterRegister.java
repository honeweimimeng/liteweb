package com.liteweb.register;

import com.liteweb.entity.WebFilter;

public class FilterRegister<T> extends Register<T>{
    @Override
    public void Register(String key, T t) {
        if(t instanceof WebFilter){
            filter_map.put(key,(WebFilter) t);
        }
    }

    @Override
    public void Cancellation(String key) {
        filter_map.remove(key);
    }
}
