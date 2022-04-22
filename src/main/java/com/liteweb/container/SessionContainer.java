package com.liteweb.container;

import com.liteweb.entity.ServerSession;
import com.liteweb.util.IDsUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Session容器
 * @author Hone
 */
public class SessionContainer implements Container{
    private static final String NAME ="SessionId";
    private static final Map<String, ServerSession> SERVER_SESSION_MAP =new ConcurrentHashMap<>();

    public ServerSession createSessionId(){
        return createSessionId(20,TimeUnit.SECONDS);
    }
    public ServerSession createSessionId(Integer time,TimeUnit timeUnit){
        String key =null;
        while (SERVER_SESSION_MAP.containsKey(key=IDsUtil.getId(NAME))){
            key=IDsUtil.getId(NAME);
        }
        ServerSession serverSession=new ServerSession();
        serverSession.setSessionId(key);
        serverSession.setTimeOut(new Date(System.currentTimeMillis()+timeUnit.toMillis(time)));
        //生成新Session
        SERVER_SESSION_MAP.put(key,serverSession);
        return serverSession;
    }
    public ServerSession getSession(String key){
        return SERVER_SESSION_MAP.get(key);
    }
}