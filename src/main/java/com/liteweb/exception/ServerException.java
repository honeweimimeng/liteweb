package com.liteweb.exception;

import com.liteweb.constant.ExceptionConstant;

/**
 * @author Hone
 */
public class ServerException extends RuntimeException{
    public ServerException(){
        super(ExceptionConstant.ERR_SERVER +"NoTip");
    }
    public ServerException(String msg){
        super(ExceptionConstant.ERR_SERVER +msg);
    }
}
