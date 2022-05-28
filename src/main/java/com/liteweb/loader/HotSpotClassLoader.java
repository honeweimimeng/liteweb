package com.liteweb.loader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

/**
 * @author Hone
 * 热加载类加载器
 */
public class HotSpotClassLoader extends ClassLoader{
    private final String classPath;

    public HotSpotClassLoader(String classpath) {
        super(ClassLoader.getSystemClassLoader());
        this.classPath = classpath;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        name = name.replace(".class","");
        byte[] data = this.loadClassData(name);
        if(data==null){
            throw new RuntimeException("热加载类"+name+"失败");
        }
        return this.defineClass(name, data, 0, data.length);
    }

    private byte[] loadClassData(String name) {
        try {
            name = name.replace(".", "\\");
            FileInputStream is = new FileInputStream(classPath+name+".class");
            int b;
            ByteArrayOutputStream baOs = new ByteArrayOutputStream();
            while((b=is.read())!=-1) {
                baOs.write(b);
            }
            is.close();
            return baOs.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
