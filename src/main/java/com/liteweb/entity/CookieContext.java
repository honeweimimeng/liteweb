package com.liteweb.entity;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Hone
 */
public class CookieContext {
    private final Map<String,Cookie> cookieMap=new HashMap<>();
    private final String host;
    public CookieContext(String host){
        this.host=host;
    }
    public Cookie getCookie(String key){
        return cookieMap.get(key);
    }
    public List<Cookie> getCookies(){
        return new ArrayList<Cookie>(cookieMap.values());
    }
    public void addCookie(String key,String value){
        Cookie cookie=new Cookie();
        cookie.setKey(key);
        cookie.setValue(value);
        cookieMap.put(key,cookie);
    }
    public void addCookie(String key, String value, int expires){
        addCookie(key,value,expires,"/");
    }
    public void addCookie(String key,String value,int expires, String path){
        addCookie(key,value,expires,path,this.host);
    }
    public void addCookie(String key,String value,int expires,String path,String domain){
        Cookie cookie=new Cookie();
        cookie.setKey(key);
        cookie.setValue(value);
        cookie.setExpires(expires);
        cookie.setPath(path);
        cookie.setDomain(domain);
        cookieMap.put(key,cookie);
    }
}
