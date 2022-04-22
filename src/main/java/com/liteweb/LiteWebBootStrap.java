package com.liteweb;

import com.liteweb.config.LiteWebConfig;
import com.liteweb.config.RunTimeConfig;
import com.liteweb.constant.ConfFileConstant;
import com.liteweb.constant.RunTimeConstant;
import com.liteweb.factory.BaseServices;
import com.liteweb.factory.LoggerFactory;
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
    public static void run(Class<?> clazz,String[] args) {
        long time=System.currentTimeMillis();
        logger.info("Server is creating...");
        new ServiceLoader().loadForConfig();
        new ServletRegisterScanner().loadServlet(clazz);
        logger.info("Server is started boot on "+LiteWebConfig.HOST+" time in "+(System.currentTimeMillis()-time)+"ms");
    }

    static class ServiceLoader{
        void loadForConfig() {
            ForkJoinPool forkJoinPool=new ForkJoinPool();
            BaseServices baseServices=new BaseServices(LiteWebConfig.HOST);
            SocketServiceServiceScanner scanner = new SocketServiceServiceScanner();
            String sslOpen=PropertiesFileUtil.getPropertiesStr(ConfFileConstant.SSL_OPEN);
            List<BaseService> baseServiceArr=new ArrayList<>();
            baseServiceArr.add(baseServices.createHttps(LiteWebConfig.HTTP_PORT));
            if(RunTimeConstant.CONF_IS_START.equals(sslOpen)){
                baseServiceArr.add(baseServices.createHttps(LiteWebConfig.HTTPS_PORT));
            }
            try {
                forkJoinPool.submit(new ServiceTask(scanner,baseServiceArr.toArray(new BaseService[0])));
            }catch (Exception e){
                throw new RuntimeException("服务引导异常");
            }
        }
    }

    static class ServiceTask extends RecursiveTask<List<BaseService>> {
        private final SocketServiceServiceScanner scanner;
        private final BaseService[] baseServices;
        public ServiceTask(SocketServiceServiceScanner scanner,BaseService... services){
            this.scanner=scanner;
            this.baseServices=services;
        }

        @Override
        protected List<BaseService> compute() {
            if(baseServices.length>1){
                ServiceTask httpTask=new ServiceTask(scanner, Arrays.copyOfRange(baseServices,0,baseServices.length/2));
                httpTask.fork();
                ServiceTask httpsTask=new ServiceTask(scanner, Arrays.copyOfRange(baseServices,baseServices.length/2,baseServices.length));
                httpsTask.fork();
                List<BaseService> resList=new ArrayList<>();
                resList.addAll(httpTask.join());
                resList.addAll(httpsTask.join());
                return resList;
            }else{
                scanner.deploy(baseServices);
                return new ArrayList<>();
            }
        }
    }
}