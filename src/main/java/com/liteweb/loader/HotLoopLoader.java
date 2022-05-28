package com.liteweb.loader;

import com.liteweb.exception.ServerException;
import com.liteweb.pool.ThreadPool;
import com.liteweb.scanner.ServletScanner;
import com.liteweb.util.FileUtil;

import javax.annotation.Resource;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Hone
 */
public class HotLoopLoader {
    private final String parentRootPath;
    private final String classRootPath;
    private final ServletScanner scanner;
    private final Map<String,Long> loadMap = new HashMap<>();

    public HotLoopLoader(Class<?> clazz,ServletScanner scanner){
        URL url = clazz.getResource("");
        URL parentUrl = clazz.getClassLoader().getResource("");
        if (url==null||parentUrl==null){
            throw new ServerException("类扫描器加载错误");
        }
        String pathSub = "\\";
        String parentRootPath=parentUrl.getPath().replace("/", pathSub);
        if(parentRootPath.startsWith(pathSub)){
            parentRootPath=parentRootPath.substring(1);
        }
        this.parentRootPath=parentRootPath;
        this.classRootPath=url.getPath();
        this.scanner=scanner;
    }

    /**
     * 执行热加载监听
     */
    public void startLoop(){
        ThreadPool.HOTSPOT_POOL.scheduleWithFixedDelay(()->{
            List<String> list=new ArrayList<>();
            FileUtil.getFile(new File(classRootPath),list);
            for(String item:list){
                getTime(item);
            }
        },1,1, TimeUnit.SECONDS);
    }

    /**
     * 获取变化时间
     * @param className
     * 类名称，扫描到的路径名
     */
    private void getTime(String className){
        File loadFile = new File(className);
        long lastModified = loadFile.lastModified();
        if (loadMap.get(className)==null) {
            loadMap.put(className,lastModified);
        }else{
            if (loadMap.get(className) != lastModified){
                //重新加载
                reSetRegister(className);
                loadMap.put(className,lastModified);
            }
        }
    }

    /**
     * 重新注册
     * @param className
     * 类名称，路径名
     */
    private void reSetRegister(String className){
        HotSpotClassLoader classLoader=new HotSpotClassLoader(parentRootPath);
        try {
            className=className.replace(parentRootPath,"").replace("\\",".");
            Class<?> clazz = classLoader.findClass(className);
            scanner.register(clazz);
        }catch (ClassNotFoundException e){
            throw new RuntimeException(className+"未找到");
        }
    }
}
