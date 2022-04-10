package com.liteweb.config;

import com.liteweb.util.PropertiesFileUtil;

import java.util.Arrays;

/**
 * 配置类对象不进行GC
 */
public abstract class ReadConfig {
    abstract String[] defaultValue();
    String loadConf(String key,int index){
        String now = PropertiesFileUtil.getPropertiesStr(key);
        return now==null||now.isEmpty() ? defaultValue()[index] : now;
    }
    Integer loadConfInt(String key,int index){
        return Integer.parseInt(loadConf(key,index));
    }
}
