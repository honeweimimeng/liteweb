package com.liteweb.entity;

import com.liteweb.connector.HttpServletConnector;
import com.liteweb.connector.ServletConnector;
import com.liteweb.container.HttpServletContainer;
import com.liteweb.enums.HttpStatusCode;
import com.liteweb.util.HttpMessageUtil;
import javafx.scene.layout.Pane;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpServletResponse implements WebServlet {
    private int code;
    private Charset charset=StandardCharsets.UTF_8;
    private String contentType="text/plain; charset="+getCharset().name();
    private CookieContext cookieContext;
    private Map<String, List<String>> header=new ConcurrentHashMap<>();
    private ByteArrayOutputStream outputStream;
    private HttpServletConnector connector;

    public CookieContext getCookieContext(String host) {
        if(cookieContext==null){
            cookieContext=new CookieContext(host);
        }
        return cookieContext;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType+"; charset="+getCharset().name();
    }

    public void setOutputStream(ByteArrayOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public int getCode() {
        return code;
    }

    public String getContentType() {
        return contentType;
    }

    public ByteArrayOutputStream getOutputStream() {
        return outputStream;
    }

    public void setHeader(Map<String, List<String>> header) {
        this.header = header;
    }

    public void addHeader(String name,String headerValue) {
        if(!header.containsKey(name)){
            header.put(name,new ArrayList<>());
        }
        header.get(name).add(headerValue);
    }

    public List<String> getHeader(String name) {
        return header.get(name);
    }

    public void setConnector(HttpServletConnector connector) {
        this.connector = connector;
    }
    public ServletConnector getConnector() {
        return connector;
    }

    @Override
    public ByteBuffer[] messageBuffers() {
        outputStream = outputStream==null ? new ByteArrayOutputStream():outputStream;
        if(code!=HttpStatusCode.SUCCESS.getCode()){
            try {
                outputStream.write((code+HttpStatusCode.getMessage(code)).getBytes(getCharset()));
            }catch (IOException e){
                ByteBuffer header_buffer=getHeader();
                return new ByteBuffer[]{header_buffer,ByteBuffer.wrap("服务发生异常".getBytes(getCharset()))};
            }
        }
        ByteBuffer header_buffer=getHeader();
        ByteBuffer body_buffer=ByteBuffer.wrap(outputStream.toByteArray());
        return new ByteBuffer[]{header_buffer,body_buffer};
    }

    private ByteBuffer getHeader(){
        addHeader("Content-Length",outputStream.toByteArray().length+"");
        addHeader("Content-Type",contentType);
        addHeader("Connection","close");
        addHeader("Date",new Date().toString());
        addHeader("Cache-Control","no-cache");
        if(cookieContext!=null){
            List<Cookie> cookies=cookieContext.getCookies();
            if(cookies.size()>0){
                cookies.forEach(item->{
                    addHeader("Set-Cookie",item.toString());
                });
            }
        }
        String headerStr= HttpMessageUtil.setResponseHeader(header,code);
        return ByteBuffer.wrap(headerStr.getBytes(getCharset()));
    }

    /**
     * 重定向到新地址
     * @param path 新地址
     */
    public void redirect(String path){
        HttpServletConnector connector= this.connector;
        new HttpServletContainer().disposeMessage(connector.getHttpServletRequest(),
                connector.getHttpServletResponse(),connector.getHttpFilter());
    }
}
