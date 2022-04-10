package com.liteweb.scanner;

import com.liteweb.anno.FilterConf;
import com.liteweb.anno.ServletConf;
import com.liteweb.context.LiteWebContext;
import com.liteweb.entity.WebFilter;
import com.liteweb.entity.WebServlet;
import com.liteweb.factory.LoggerFactory;
import java.util.logging.Logger;

public class ServletRegisterScanner extends ServletScanner{
    private static final Logger logger= LoggerFactory.createException("ServletRegister");
    /**
     * 注册组件对应字节码对象的实例对象
     * @param clazz_item 单个字节码对象
     */
    @Override
    void register(Class<?> clazz_item){
        LiteWebContext context=LiteWebContext.getInstance();
        Object instance;
        try {
            instance=clazz_item.newInstance();
            //注册Servlet
            if(instance instanceof WebServlet){
                ServletConf servletConf =clazz_item.getAnnotation(ServletConf.class);
                if(servletConf ==null){
                    logger.severe(clazz_item.getName()+"注册失败，无servlet注解");
                    return;
                }
                context.getServletRegister().Register(servletConf.path(),(WebServlet) instance);
            }
            //注册拦截器
            if(instance instanceof WebFilter){
                FilterConf filterConf =clazz_item.getAnnotation(FilterConf.class);
                if(filterConf ==null){
                    logger.severe(clazz_item.getName()+"拦截器注册失败，无filter注解");
                    return;
                }
                //加载拦截器
                context.getFilterRegister().Register(filterConf.path(),(WebFilter) instance);
            }
        }catch (IllegalAccessException|InstantiationException e){
            logger.severe("类"+clazz_item.getName()+"无法被实例化");
        }
    }
}
