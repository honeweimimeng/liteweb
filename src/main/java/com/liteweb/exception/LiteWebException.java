package com.liteweb.exception;

import com.liteweb.constant.ExceptionConstant;

public class LiteWebException extends RuntimeException{
    public LiteWebException(){
        super(ExceptionConstant.ERR_ROOT_WEB +"NoTips");
    }
    public LiteWebException(String msg){
        super(ExceptionConstant.ERR_ROOT_WEB +msg);
    }
}
