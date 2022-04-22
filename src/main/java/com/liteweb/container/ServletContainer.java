package com.liteweb.container;

import com.liteweb.anno.SessionConf;
import com.liteweb.entity.Cookie;
import com.liteweb.entity.ServerSession;
import com.liteweb.entity.WebFilter;
import com.liteweb.entity.WebServlet;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * @author Hone
 */
public abstract class ServletContainer implements Container{
   private static final SessionContainer SESSION_CONTAINER =new SessionContainer();

   /**
    * 处理Servlet报文消息
    * @param request 请求报文
    * @param response 响应报文
    * @param filter 连接器
    */
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
            ServerSession serverSession=cookie.getKey() == null ? null : SESSION_CONTAINER.getSession(cookie.getKey());
            ServerSession setServerSession =serverSession==null ? SESSION_CONTAINER.createSessionId():serverSession;
            try {
               field.setAccessible(true);
               field.set(webServlet,setServerSession);
            }catch (Exception e){
               throw new RuntimeException("Session设置失败");
            }
            if(serverSession==null){
               return setServerSession;
            }
            //刷新session的过期时间
            serverSession.setTimeOut(new Date(serverSession.getTimeOut().getTime()+5*60*1000));
            return serverSession;
         }
      }
      return null;
   }
}