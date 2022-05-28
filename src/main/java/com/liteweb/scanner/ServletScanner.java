package com.liteweb.scanner;

import com.liteweb.constant.ConfFileConstant;
import com.liteweb.exception.ServerException;
import java.net.URL;

/**
 * @author Hone
 */
public abstract class ServletScanner {
    /**
     * 记载servlet
     * @param clazz 启动类字节码对象
     */
    public void loadServlet(Class<?> clazz){
        ClazzScanner clazzScanner=ClazzScanner.getInstance();
        //扫描servlet,Filter等class文件
        Class<?>[] servletsClazz =clazzScanner.findAll(clazz);
        for(Class<?> clazzItem :servletsClazz){
            if(!clazzItem.getName().equals(clazz.getName())){
                register(clazzItem);
            }
        }
    }

    /**
     * 注册class
     * @param clazzItem 获取到的class单体
     */
    public abstract void register(Class<?> clazzItem);
}
