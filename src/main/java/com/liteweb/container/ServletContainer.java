package com.liteweb.container;

import com.liteweb.anno.SessionConf;
import com.liteweb.entity.Cookie;
import com.liteweb.entity.ServerSession;
import com.liteweb.entity.WebFilter;
import com.liteweb.entity.WebServlet;
import java.lang.reflect.Field;
import java.util.Date;

public abstract class ServletContainer implements Container{
   private static final SessionContainer sessionContainer=new SessionContainer();
   public abstract void disposeMessage(WebServlet request, WebServlet response , WebFilter filter);

   /**
    处理Session
    * @param webServlet Http请求处理实体
    * @param cookie SessionID信息
    * @return 新生成SessionID
    */
   ServerSession disposeSession(WebServlet webServlet, Cookie cookie){
      for(Field field:webServlet.getClass().getDeclaredFields()){
         if(field.getAnnotation(SessionConf.class)!=null&&field.getType()==ServerSession.class){
            ServerSession serverSession=cookie.getKey() == null ? null : sessionContainer.getSession(cookie.getKey());
            ServerSession set_serverSession=serverSession==null ? sessionContainer.createSessionID():serverSession;
            try {
               field.setAccessible(true);
               field.set(webServlet,set_serverSession);
            }catch (Exception e){
               throw new RuntimeException("Session设置失败");
            }
            if(serverSession==null){
               return set_serverSession;
            }
            //刷新session的过期时间
            serverSession.setTimeOut(new Date(serverSession.getTimeOut().getTime()+5*60*1000));
            return serverSession;
         }
      }
      return null;
   }
}