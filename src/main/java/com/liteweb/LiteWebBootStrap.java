package com.liteweb;

import com.liteweb.config.LiteWebConfig;
import com.liteweb.constant.ConfFileConstant;
import com.liteweb.factory.BaseServices;
import com.liteweb.factory.LoggerFactory;
import com.liteweb.scanner.*;
import com.liteweb.service.BaseService;
import com.liteweb.util.PropertiesFileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 服务引导类
 */
public class LiteWebBootStrap {
    private static final Logger logger= LoggerFactory.createInfo("ServerRoot");
    /**
     * 启动类，不可实例化
     */
    private LiteWebBootStrap(){}

    /**
     * 加载运行器
     * @param args main参数
     * @param clazz 引导基类
     */
    public static void run(Class<?> clazz,String[] args) {
        long time=System.currentTimeMillis();
        logger.info("Server is creating...");
        new ServiceLoader().LoadForConfig();
        new ServletRegisterScanner().loadServlet(clazz);
        logger.info("Server is started boot on "+LiteWebConfig.host+" time in "+(System.currentTimeMillis()-time)+"ms");
    }

    static class ServiceLoader{
        void LoadForConfig(){
            String SSL_OPEN=PropertiesFileUtil.getPropertiesStr(ConfFileConstant.SSL_OPEN);
            BaseServices baseServices=new BaseServices(LiteWebConfig.host);
            List<BaseService> baseServiceList=new ArrayList<>();
            baseServiceList.add(baseServices.createHttp(LiteWebConfig.http_port));
            if("true".equals(SSL_OPEN)){
                baseServiceList.add(baseServices.createHttps(LiteWebConfig.https_port));
            }
            new SocketServiceServiceScanner().Deploy(baseServiceList.toArray(new BaseService[0]));
        }
    }
}