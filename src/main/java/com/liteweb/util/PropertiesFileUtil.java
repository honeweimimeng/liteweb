package com.liteweb.util;

import com.liteweb.exception.FileFoundException;
import com.liteweb.exception.LoadPropertiesException;
import com.liteweb.factory.LoggerFactory;
import com.liteweb.factory.PropertiesFactory;
import com.liteweb.constant.ConfFileConstant;
import com.liteweb.constant.OperationConstant;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author Hone
 */
public class PropertiesFileUtil {
    private static final Logger logger= LoggerFactory.createException("PropertiesLoad");
    private static final Logger logger_success= LoggerFactory.createInfo("PropertiesLoadSuccess");
    private static final Properties PROPERTIES;

    //获取单例工厂的Properties引用
    static {
        PROPERTIES = PropertiesFactory.createProperties();
        if(PROPERTIES==null){
            logger.severe("资源目录下无"+ConfFileConstant.CONF_FILE_NAME+"配置文件");
        }
    }

    /**
     * 获取配置的运行模式
     * @return 运行模式 OperationConstant.XX_RUN_MODEL
     */
    public static String getOperationModel() {
        String runModeConf = PROPERTIES ==null ? null:PROPERTIES.getProperty(ConfFileConstant.OPERATION_MODEL_NAME);
        if(runModeConf==null||runModeConf.isEmpty()){
            return OperationConstant.NIO_RUN_MODEL;
        }
        return runModeConf;
    }

    /**
     * 获取str属性
     * @param name
     * 属性名称
     * @return String
     */
    public static String getPropertiesStr(String name){
        return PROPERTIES==null ? null:PROPERTIES.getProperty(name);
    }

    /**
     * 获取 Properties 文件对象
     * @param fileName
     * 文件名称
     * @return Properties
     */
    public static Properties getProperties(String fileName) {
        //先读取最外层目录的，JAR同级
        try {
            Properties properties=realGetProperties(System.getProperty(ConfFileConstant.FILE_COME_MODEL) + File.separator + fileName);
            logger_success.info("成功加载配置文件："+fileName+"，来自=>外部资源");
            return properties;
        } catch (IOException e) {
            //无加载文件，读取资源目录下的
            try {
                Properties properties=realGetProperties(System.getProperty(ConfFileConstant.FILE_COME_MODEL) +
                        File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator + fileName);
                logger_success.info("成功加载配置文件："+fileName+"，来自=>内部资源文件夹");
                return properties;
            }catch (IOException not){
                return null;
            }
        }
    }

    /**
     * 通过路径拿到对象
     * @param path 路径
     * @return Properties对象
     * @throws IOException 未找到文件
     */
    private static Properties realGetProperties(String path) throws IOException{
        Properties properties = new Properties();
        InputStream in = new FileInputStream(path);
        properties.load(in);
        return properties;
    }
}