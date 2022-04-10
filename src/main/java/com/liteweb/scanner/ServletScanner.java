package com.liteweb.scanner;

import com.liteweb.exception.ServerException;
import java.net.URL;

public abstract class ServletScanner {
    /**
     * 记载servlet
     * @param clazz 启动类字节码对象
     */
    public void loadServlet(Class<?> clazz){
        URL url=clazz.getResource("/");
        if(url==null){
            throw new ServerException("路径配置错误，无法扫描映射文件");
        }
        ClazzScanner clazzScanner=ClazzScanner.getInstance();
        //扫描servlet,Filter等class文件
        Class<?>[] servlets_clazz=clazzScanner.findAll(url.getPath());
        for(Class<?> clazz_item:servlets_clazz){
            if(!clazz_item.getName().equals(clazz.getName())){
                register(clazz_item);
            }
        }
    }
    abstract void register(Class<?> clazz_item);
}
