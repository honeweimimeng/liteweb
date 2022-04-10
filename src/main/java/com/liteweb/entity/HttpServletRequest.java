package com.liteweb.entity;

import com.liteweb.connector.HttpServletConnector;
import com.liteweb.connector.ServletConnector;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpServletRequest implements WebServlet {

    private String method;

    private String protocol;

    private String requestURI;

    private String host;

    private String connection;

    private Map<String, String> pragma;

    private String userAgent;

    private String accept;

    private String referer;

    private String acceptEncoding;

    private String acceptLanguage;

    private int contentLength;

    private String contentType;

    private CookieContext cookieContext;

    private ByteArrayInputStream bodyStream;

    private Charset charset= StandardCharsets.UTF_8;

    private HttpServletConnector connector;

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setBodyStream(ByteArrayInputStream bodyStream) {
        this.bodyStream = bodyStream;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public void setAcceptEncoding(String acceptEncoding) {
        this.acceptEncoding = acceptEncoding;
    }

    public void setAcceptLanguage(String acceptLanguage) {
        this.acceptLanguage = acceptLanguage;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public void setCookieContext(CookieContext cookieContext) {
        this.cookieContext = cookieContext;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setPragma(Map<String, String> pragma) {
        this.pragma = pragma;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public ByteArrayInputStream getBodyStream() {
        return bodyStream;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public String getHost() {
        return host;
    }

    public CookieContext getCookieContext() {
        return cookieContext;
    }

    public Map<String, String> getPragma() {
        return pragma;
    }

    public String getAccept() {
        return accept;
    }

    public String getAcceptEncoding() {
        return acceptEncoding;
    }

    public String getAcceptLanguage() {
        return acceptLanguage;
    }

    public String getConnection() {
        return connection;
    }

    public String getMethod() {
        return method;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getReferer() {
        return referer;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setConnector(HttpServletConnector connector) {
        this.connector = connector;
    }

    public ServletConnector getConnector() {
        return connector;
    }

    @Override
    public ByteBuffer[] messageBuffers() {
        return new ByteBuffer[0];
    }
}
