package com.liteweb.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hone
 */
public class FileUtil {

    /**
     * 获取类的全限定名
     * @param path 项目目录
     * @return String[]全限名数组
     */
    public static String[] getClassFileName(String path){
        path=path.replaceFirst("/","");
        List<String> list=new ArrayList<>();
        List<String> res=new ArrayList<>();
        getFile(new File(path),list);
        for(String item:list){
            res.add(item.replace("\\","/").replace(path,"")
                    .replace("/",".").replace(".class",""));
        }
        return res.toArray(new String[list.size()]);
    }

    /**
     * 获取文件路径列表
     * @param file 根目录文件对象
     * @param list 结果列表
     */
    public static void getFile(File file,List<String> list){
        if(file.isDirectory()){
            File[] files=file.listFiles();
            if(files!=null){
                for(File item:files){
                    getFile(item,list);
                }
            }
        }else{
            if(file.getName().endsWith(".class")){
                list.add(file.getAbsolutePath());
            }
        }
    }


}