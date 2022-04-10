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
 */
public class HttpService extends SocketService{
    //通信管道
    private final Channel channel;
    //Socket通信管道
    protected Channel socketChannel;
    //服务对应包含的container
    private final ServletContainer servletContainer=new HttpServletContainer();
    //服务内部聚合的连接
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

    @Override
    public Channel getSocketChannel() {
        return socketChannel;
    }

    /**
     * 处理socket传输信息,子组件获取Http报文,子组件处理Http行为
     * @return ServiceHandler
     */
    @Override
    public ServiceHandler serviceHandler() {
        return (channel)->{
            long start=System.currentTimeMillis();
            this.socketChannel=channel;
            //获取请求报文,委派子组件完成
            HttpServletConnector httpServletConnector=new HttpServletConnector(new HttpServletBuilder(channel));
            HttpServletRequest request=realHttpHandler(httpServletConnector);
            logger.info("Method:"+request.getMethod()+" -- Path:"+request.getRequestURI()+" -- 耗费时间："+(System.currentTimeMillis()-start)+"ms");
        };
    }

    protected HttpServletRequest realHttpHandler(HttpServletConnector connector){
        servletConnectors().add(connector);
        HttpServletRequest request=connector.getHttpServletRequest();
        HttpServletResponse response=connector.getHttpServletResponse();
        //委派给container进行行为处理
        servletContainer().disposeMessage(request,response,connector.getHttpFilter());
        connector.response(getSocketChannel(),response.messageBuffers());
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
    public void Accept(Channel channel) {}
}
