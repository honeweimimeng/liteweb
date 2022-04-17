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
 */
public class SessionContainer implements Container{
    private static final String Name="SessionId";
    private static final Map<String, ServerSession> serverSessionMap=new ConcurrentHashMap<>();

    public ServerSession createSessionID(){
        return createSessionID(20,TimeUnit.SECONDS);
    }
    public ServerSession createSessionID(Integer time,TimeUnit timeUnit){
        String Key=null;
        while (serverSessionMap.containsKey(Key=IDsUtil.getId(Name))){
            Key=IDsUtil.getId(Name);
        }
        ServerSession serverSession=new ServerSession();
        serverSession.setSessionID(Key);
        serverSession.setTimeOut(new Date(System.currentTimeMillis()+timeUnit.toMillis(time)));
        //生成新Session
        serverSessionMap.put(Key,serverSession);
        return serverSession;
    }
    public ServerSession getSession(String key){
        return serverSessionMap.get(key);
    }
}