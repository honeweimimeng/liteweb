package com.liteweb.exception;

import com.liteweb.constant.ExceptionConstant;

/**
 * @author Hone
 */
public class FileFoundException extends LiteWebException{
    public FileFoundException(String msg){
        super(ExceptionConstant.ERR_FILE_FOUND +msg);
    }
}
