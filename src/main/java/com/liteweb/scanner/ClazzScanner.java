package com.liteweb.scanner;

import com.liteweb.exception.ServerException;
import com.liteweb.factory.LoggerFactory;
import com.liteweb.util.FileUtil;
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

    public Class<?>[] findAll(String absoluteName) {
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
}
