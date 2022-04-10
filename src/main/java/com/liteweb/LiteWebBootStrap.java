package com.liteweb;

import com.liteweb.config.LiteWebConfig;
import com.liteweb.factory.BaseServices;
import com.liteweb.factory.LoggerFactory;
import com.liteweb.scanner.*;
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
        BaseServices baseServices=new BaseServices(LiteWebConfig.host);
        new SocketServiceServiceScanner().Deploy(baseServices.createHttp(8080),baseServices.createHttps(8081));
        new ServletRegisterScanner().loadServlet(clazz);
        logger.info("Server is started in "+(System.currentTimeMillis()-time)+"ms");
    }
}