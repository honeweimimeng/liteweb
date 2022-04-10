package com.liteweb.config;

import com.liteweb.util.PropertiesFileUtil;

import java.util.Arrays;

/**
 * 配置类对象不进行GC
 */
public abstract class ReadConfig {
    abstract String[] defaultValue();
    String[] loadConf(int start,int limit,String... t){
        String[] strings=new String[limit-start];
        for (int i = start; i < limit; i++) {
            String now = PropertiesFileUtil.getPropertiesStr(t[i-start]);
            strings[i-start] = now==null||now.isEmpty() ? defaultValue()[i]:now;
        }
        return strings;
    }
    Integer[] loadConfInt(int start,int limit,String... t){
        String[] strings = loadConf(start,limit,t);
        Integer[] integers = new Integer[strings.length];
        for (int i = 0; i < strings.length; i++) {
            integers[i]=Integer.parseInt(strings[i]);
        }
        return integers;
    }
}
