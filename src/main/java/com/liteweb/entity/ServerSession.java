package com.liteweb.entity;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Hone
 */
public class ServerSession {
    private String sessionId;
    private String value;
    private Date timeOut;
    private final Map<String,Content> contents=new ConcurrentHashMap<>();

    public void setValue(String value) {
        this.value = value;
    }

    public void addContents(Content content) {
        this.contents.put(content.key,content);
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setTimeOut(Date timeOut) {
        this.timeOut = timeOut;
    }

    public String getValue() {
        return value;
    }

    public Date getTimeOut() {
        return timeOut;
    }

    public Content findContents(String key) {
        return contents.get(key);
    }

    public String getSessionId() {
        return sessionId;
    }

    public static class Content{
        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}