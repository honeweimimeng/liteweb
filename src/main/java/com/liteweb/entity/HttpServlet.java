package com.liteweb.entity;

import com.liteweb.enums.HttpStatusCode;
import java.nio.ByteBuffer;

/**
 * @author Hone
 */
public abstract class HttpServlet implements WebServlet{
    public void init(HttpServletRequest request){}
    public void doGet(HttpServletRequest request,HttpServletResponse response){
        response.setCode(HttpStatusCode.NOT_ALLOWED.getCode());
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response){
        response.setCode(HttpStatusCode.NOT_ALLOWED.getCode());
    }
    public void doPut(HttpServletRequest request,HttpServletResponse response){
        response.setCode(HttpStatusCode.NOT_ALLOWED.getCode());
    }
    public void doDelete(HttpServletRequest request,HttpServletResponse response){
        response.setCode(HttpStatusCode.NOT_ALLOWED.getCode());
    }
    public void destroy(){}
    @Override
    public ByteBuffer[] messageBuffers() {
        return new ByteBuffer[0];
    }
}