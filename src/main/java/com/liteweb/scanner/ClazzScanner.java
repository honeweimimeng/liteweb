package com.liteweb.scanner;

import com.liteweb.exception.ServerException;
import com.liteweb.factory.LoggerFactory;
import com.liteweb.util.FileUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public class ClazzScanner{
    private static final Logger logger = LoggerFactory.createInfo("类扫描器扫描异常");
    private static volatile ClazzScanner clazzScanner;

    private ClazzScanner(){
        if(clazzScanner!=null){
            throw new ServerException("单例组件不可实例化");
        }
    }

    public static ClazzScanner getInstance() {
        if(clazzScanner==null){
            synchronized (ClazzScanner.class){
                if(clazzScanner==null){
                    clazzScanner=new ClazzScanner();
                }
            }
        }
        return clazzScanner;
    }

    /**
     * 扫描字节码数组
     * @param clazz 根字节码
     * @return 字节码数组
     */
    public Class<?>[] findAll(Class<?> clazz) {
        URL url=clazz.getResource("/");
        return url == null ? findToJar(clazz.getProtectionDomain()
                .getCodeSource().getLocation().getPath(),clazz.getName())
                :findToResource(url.getPath());
    }

    /**
     * 本地项目扫描字节码数组
     * @param absoluteName 根字节码绝对路径名称
     * @return 字节码数组
     */
    private Class<?>[] findToResource(String absoluteName){
        String[] names = FileUtil.getClassFileName(absoluteName);
        Class<?>[] clazzArr=new Class<?>[names.length];
        for (int i = 0; i < names.length; i++) {
            try {
                clazzArr[i]=Class.forName(names[i]);
            }catch (ClassNotFoundException e){
                logger.severe("类"+names[i]+"未找到，无法加载");
            }
        }
        return clazzArr;
    }

    /**
     * Jar包扫描字节码数组
     * @param absoluteName Jar包绝对路径
     * @param name 根字节码名称（类选限定名称）
     * @return 字节码数组
     */
    private Class<?>[] findToJar(String absoluteName,String name){
        List<Class<?>> resList=new ArrayList<>();
        String path_name=name.substring(0,name.lastIndexOf("."));
        JarFile jar = null;
        try {
            jar = new JarFile(absoluteName);
            Enumeration<JarEntry> entryEnumeration = jar.entries();
            while (entryEnumeration.hasMoreElements()) {
                String entity_name = entryEnumeration.nextElement().getName().replace("/",".");
                if(entity_name.contains(path_name)&&
                        "class".equals(entity_name.substring(entity_name.lastIndexOf(".")+1))){
                    if(!name.equals(entity_name=entity_name.substring(0,entity_name.lastIndexOf(".")))){
                        try {
                            resList.add(Class.forName(entity_name));
                        }catch (ClassNotFoundException e){
                            logger.severe("类"+entity_name+"未找到，无法加载");
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new ServerException("JAR扫描失败，无法扫描Servlet");
        } finally {
            if(jar!=null){
                try {
                    // 关闭jar文件
                    jar.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return resList.toArray(new Class[0]);
    }
}
