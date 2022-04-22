package com.liteweb.entity;

import com.liteweb.constant.RunTimeConstant;
import com.liteweb.constant.ServerConstant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Cookie {
    private String key;
    private Object value;
    private String path;
    private String domain;
    private int expires;

    public Cookie(){}

    public Cookie(String key,String value){
        this.key=key;
        this.value=value;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
     public String getDomain() {
        return domain;
    }

    public int getExpires() {
        return expires;
    }

    public String getKey() {
        return key;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.SECOND,6*60*60+expires);
        SimpleDateFormat format=new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        return key + ServerConstant.EQUALS_STR + value
                + ServerConstant.END_STR + RunTimeConstant.NULL_STR
                + "path=" + path + ServerConstant.END_STR + RunTimeConstant.NULL_STR
                + "expires=" + format.format(calendar.getTime())
                + ServerConstant.END_STR + RunTimeConstant.NULL_STR
                + "domain=" + domain;
    }
}
