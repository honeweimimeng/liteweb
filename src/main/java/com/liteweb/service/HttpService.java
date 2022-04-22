package com.liteweb.service;

import com.liteweb.builder.HttpServletBuilder;
import com.liteweb.connector.HttpServletConnector;
import com.liteweb.connector.ServletConnector;
import com.liteweb.container.HttpServletContainer;
import com.liteweb.container.ServletContainer;
import com.liteweb.entity.HttpServletRequest;
import com.liteweb.entity.HttpServletResponse;
import com.liteweb.factory.LoggerFactory;
import java.nio.channels.Channel;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * Http服务
 * @author Hone
 */
public class HttpService extends SocketService{
    private final Channel channel;
    private final ServletContainer servletContainer=new HttpServletContainer();
    private final List<ServletConnector> servletConnectors=new CopyOnWriteArrayList<>();
    private static final Logger logger= LoggerFactory.createInfo("HttpServlet预处理");

    public HttpService(Channel channel){
        this.channel=channel;
    }

    /**
     * 获取连接列表
     * @return List<ServletConnector>
     */
    public List<ServletConnector> getServletConnectors() {
        return servletConnectors;
    }

    /**
     * 通信管道
     * @return Channel
     */
    @Override
    public Channel getChannel() {
        return channel;
    }

    /**
     * 处理socket传输信息,子组件获取Http报文,子组件处理Http行为
     * @return ServiceHandler
     */
    @Override
    public ServiceHandler serviceHandler(Object... objects) {
        return (channel)->{
            long start=System.currentTimeMillis();
            //获取请求报文,委派子组件完成
            HttpServletConnector httpServletConnector=new HttpServletConnector(new HttpServletBuilder(channel));
            HttpServletRequest request=realHttpHandler(httpServletConnector);
            logger.info("Method:"+request.getMethod()+" -- Path:"+request.getRequestUri()+" -- 耗费时间："+(System.currentTimeMillis()-start)+"ms");
        };
    }

    protected HttpServletRequest realHttpHandler(HttpServletConnector connector){
        servletConnectors().add(connector);
        HttpServletRequest request=connector.getHttpServletRequest();
        HttpServletResponse response=connector.getHttpServletResponse();
        //委派给container进行行为处理
        servletContainer().disposeMessage(request,response,connector.getHttpFilter());
        connector.response(connector.getSocketChannel(),response.messageBuffers());
        return request;
    }

    @Override
    ServletContainer servletContainer() {
        return servletContainer;
    }

    @Override
    List<ServletConnector> servletConnectors() {
        return servletConnectors;
    }

    @Override
    public Object accept(Channel channel) {
        return null;
    }
}
