package com.liteweb.util;

import com.liteweb.exception.FileFoundException;
import com.liteweb.exception.LoadPropertiesException;
import com.liteweb.factory.PropertiesFactory;
import com.liteweb.constant.ConfFileConstant;
import com.liteweb.constant.OperationConstant;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesFileUtil {
    private static final Properties properties;

    //获取单例工厂的Properties引用
    static {
        properties = PropertiesFactory.createProperties();
        if(properties==null){
            throw new FileFoundException("资源目录下无"+ConfFileConstant.CONF_FILE_NAME+"配置文件");
        }
    }

    /**
     * 获取配置的运行模式
     * @return 运行模式 OperationConstant.XX_RUN_MODEL
     */
    public static String getOperationModel() {
        String run_mode_conf = properties.getProperty(ConfFileConstant.OPERATION_MODEL_NAME);
        if(run_mode_conf==null||run_mode_conf.isEmpty()){
            return OperationConstant.AIO_RUN_MODEL;
        }
        return run_mode_conf;
    }

    /**
     * 获取int属性
     * @param name
     * 属性名称
     * @return Integer
     */
    public static Integer getPropertiesInt(String name){
        Integer res=null;
        String str_res=properties.getProperty(name);
        if(str_res!=null&&!str_res.isEmpty()){
            try {
                res=Integer.parseInt(str_res);
            }catch (NumberFormatException e){
                throw new LoadPropertiesException("加载属性"+name+"错误，不是合适的Int类型");
            }
        }
        return res;
    }

    /**
     * 获取str属性
     * @param name
     * 属性名称
     * @return String
     */
    public static String getPropertiesStr(String name){
        return properties.getProperty(name);
    }

    /**
     * 获取 Properties 文件对象
     * @param fileName
     * 文件名称
     * @return Properties
     */
    public static Properties getProperties(String fileName) {
        //先读取config目录的，没有再加载classpath的
        try {
            String path = System.getProperty(ConfFileConstant.FILE_COME_MODEL) +
                    File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator;
            Properties properties = new Properties();
            InputStream in = new FileInputStream(path + fileName);
            properties.load(in);
            return properties;
        } catch (IOException e) {
            //无加载文件
            return null;
        }
    }
}