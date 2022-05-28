package com.liteweb;

import com.liteweb.config.LiteWebConfig;
import com.liteweb.config.RunTimeConfig;
import com.liteweb.constant.ConfFileConstant;
import com.liteweb.constant.RunTimeConstant;
import com.liteweb.factory.BaseServices;
import com.liteweb.factory.LoggerFactory;
import com.liteweb.loader.HotLoopLoader;
import com.liteweb.loader.HotSpotClassLoader;
import com.liteweb.scanner.*;
import com.liteweb.service.BaseService;
import com.liteweb.util.PropertiesFileUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Logger;

/**
 * 服务引导类
 * @author Hone
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
    public static HotLoopLoader run(Class<?> clazz, String[] args) {
        long time=System.currentTimeMillis();
        logger.info("Server is creating...");
        new ServiceLoader().loadForConfig();
        ServletScanner scanner = new ServletRegisterScanner();
        scanner.loadServlet(clazz);
        HotLoopLoader hotLoopLoader=new HotLoopLoader(clazz,scanner);
        logger.info("Server is started boot on "+LiteWebConfig.HOST+" time in "+(System.currentTimeMillis()-time)+"ms");
        return hotLoopLoader;
    }

    static class ServiceLoader{
        void loadForConfig() {
            BaseServices baseServices=new BaseServices(LiteWebConfig.HOST);
            SocketServiceServiceScanner scanner = new SocketServiceServiceScanner();
            String sslOpen=PropertiesFileUtil.getPropertiesStr(ConfFileConstant.SSL_OPEN);
            List<BaseService> baseServiceArr=new ArrayList<>();
            baseServiceArr.add(baseServices.createHttp(LiteWebConfig.HTTP_PORT));
            if(RunTimeConstant.CONF_IS_START.equals(sslOpen)){
                baseServiceArr.add(baseServices.createHttps(LiteWebConfig.HTTPS_PORT));
            }
            scanner.deploy(baseServiceArr.toArray(new BaseService[0]));
        }
    }
}