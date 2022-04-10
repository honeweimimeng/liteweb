package com.liteweb.enums;

public enum HttpStatusCode {
    CONTINUE(100,"资源发送中，等待响应","Continue"),
    SUCCESS(200,"请求成功","OK"),
    REDIRECT(302,"重定向到了新的地址","Found"),
    BAD(400,"报文格式不支持","Bad Request"),
    NOT_ALLOWED(403,"不支持此方法","Method not allowed"),
    NOTFOUND(404,"资源未找到","Not Found"),
    ERROR(500,"服务器内部错误","Internal Server Error"),
    UNAVAILABLE(503,"服务无法工作","Service Unavailable");
    private final Integer code;
    private final String message;
    private final String tips;
    private HttpStatusCode(int code,String message,String tips){
        this.code=code;
        this.message=message;
        this.tips=tips;
    }

    public String getTips() {
        return tips;
    }

    public String getMessage() {
        return message;
    }
    public static String getMessage(int code) {
        for(HttpStatusCode statusCode:HttpStatusCode.values()){
            if(code == statusCode.getCode()){
                return statusCode.getMessage();
            }
        }
        return "状态码不存在";
    }
    public static String getTips(int code) {
        for(HttpStatusCode statusCode:HttpStatusCode.values()){
            if(code == statusCode.getCode()){
                return statusCode.getTips();
            }
        }
        return "状态码不存在";
    }

    public Integer getCode() {
        return code;
    }
}