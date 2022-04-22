package com.liteweb.enums;

/**
 * @author Hone
 */

public enum HttpMethodName {
    GET("GET"),POST("POST"),PUT("PUT"),DELETE("DELETE");
    private final String name;
    private HttpMethodName(String name){
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public static HttpMethodName getInstance(String key){
        for (HttpMethodName methodName:HttpMethodName.values()){
            if(methodName.getName().equals(key)){
                return methodName;
            }
        }
        return null;
    }
}
