package com.liteweb.exception;

import com.liteweb.constant.ExceptionConstant;

public class LoadPropertiesException extends RuntimeException{
    public LoadPropertiesException(){
        super(ExceptionConstant.ERR_PROPERTIES +"NoTip");
    }
    public LoadPropertiesException(String msg){
        super(ExceptionConstant.ERR_PROPERTIES +msg);
    }
}
