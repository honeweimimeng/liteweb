package com.liteweb.builder;

import com.liteweb.config.RunTimeConfig;
import com.liteweb.constant.ServerConstant;
import com.liteweb.entity.HttpServletRequest;
import com.liteweb.entity.HttpServletResponse;
import com.liteweb.entity.WebServlet;
import com.liteweb.util.HttpMessageUtil;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutionException;

public class HttpServletBuilder extends ServletBuilder{
    protected final Channel channel;
    public HttpServletBuilder(Channel channel){
        this.channel=channel;
    }

    /**
     * 构建请求实体
     * @return HttpServletRequest
     */
    @Override
    public WebServlet buildRequest() {
        if(channel==null){
            throw new RuntimeException("通信管道为空");
        }
        HttpServletRequest request=new HttpServletRequest();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(RunTimeConfig.servlet_header_length);
        readByteBuffer(request,byteBuffer);
        return request;
    }

    /**
     * 读取字节缓冲区
     * @param byteBuffer 字节缓冲实例
     */
    protected void readByteBuffer(HttpServletRequest request, ByteBuffer byteBuffer){
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        try {
            int resSize=byteBuffer.position();
            if(resSize==0){
                resSize=selChannelToBuffer(byteBuffer);
            }
            byteBuffer.flip();
            byte[] header = new byte[byteBuffer.remaining()];
            byteBuffer.get(header,0,header.length);
            String data=new String(header, request.getCharset());
            //读取请求头
            readHeader(data,request);
            int body_splice_index=data.indexOf(ServerConstant.HttpNoneLineTag);
            if(body_splice_index<0){
                throw new RuntimeException("报文错误，缺少参数");
            }
            String bodyData=data.substring(body_splice_index);
            if(resSize<RunTimeConfig.servlet_header_length){
                //小于最大头部报文限制，无后续内容，设置请求体内容
                if(!bodyData.isEmpty()){
                    outputStream.write(bodyData.getBytes(request.getCharset()));
                    request.setBodyStream(new ByteArrayInputStream(outputStream.toByteArray()));
                }
                return;
            }
            //读取后续内容,设置请求体流
            byte[] bodyBytes = bodyData.getBytes(request.getCharset());
            int remainLength = request.getContentLength() - bodyBytes.length;
            byte[] remainData = additiveBody(remainLength);
            if(remainData==null){
                throw new RuntimeException("读取请求体错误");
            }
            outputStream.write(bodyBytes);
            outputStream.write(remainData);
            request.setBodyStream(new ByteArrayInputStream(outputStream.toByteArray()));
        }catch (IOException | ExecutionException | InterruptedException e){
            throw new RuntimeException("实例化请求报文失败"+e.getMessage());
        }
    }

    /**
     * 格式化请求头，设置实体字段
     * @param data 头部字符串上下文
     * @param request 请求实体
     */
    private void readHeader(String data, HttpServletRequest request){
        int firstIndex=data.indexOf(ServerConstant.HttpNoneLineTag);
        if(firstIndex<0){
            throw new RuntimeException("报文错误，缺少参数");
        }
        String headerData=data.substring(0,firstIndex);
        String[] filed_arr=headerData.split(ServerConstant.HttpLineTag);
        boolean isFirstLine=true;
        if (filed_arr.length < 2) {
            throw new RuntimeException("报文错误，缺少参数");
        }
        for(String filed:filed_arr){
            if(isFirstLine){
                isFirstLine=false;
                //行首处理
                HttpMessageUtil.setHeaderFirstLine(filed,request);
            }else{
                //其他行头部参数处理
                HttpMessageUtil.setOtherFiled(filed,request);
            }
        }
    }

    /**
     * 选择不同通信管道，管道肯能为异步或同步
     * @param byteBuffer 字节缓冲区
     * @return 当前读取大小
     * @throws IOException IO读取异常
     * @throws ExecutionException 异步管道结果异常
     * @throws InterruptedException 一部管道结果异常
     */
    protected int selChannelToBuffer(ByteBuffer byteBuffer) throws IOException,
            ExecutionException, InterruptedException {
        int loopReadSize;
        if(channel instanceof AsynchronousSocketChannel){
            //异步IO
            AsynchronousSocketChannel asy_channel=(AsynchronousSocketChannel) channel;
            //等待返回结果
            loopReadSize=asy_channel.read(byteBuffer).get();
        }else if(channel instanceof SocketChannel){
            //同步IO
            SocketChannel syn_channel=(SocketChannel) channel;
            loopReadSize=syn_channel.read(byteBuffer);
        }else{
            throw new RuntimeException("管道类型错误");
        }
        return loopReadSize;
    }

    /**
     * 溢出body处理
     * @param length 需要读取的溢出请求体的长度
     * @return byte[]
     */
    private byte[] additiveBody(int length) throws IOException {
        int directSize = Math.min(length, 1024);
        ByteBuffer byteBuffer=ByteBuffer.allocateDirect(directSize);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            int readSize = 0;
            while (readSize < length) {
                try {
                    int loopReadSize = selChannelToBuffer(byteBuffer);
                    readSize += loopReadSize;
                    byteBuffer.flip();
                    byte[] data = new byte[byteBuffer.remaining()];
                    byteBuffer.get(data);
                    outputStream.write(data);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    byteBuffer.flip();
                    byteBuffer.clear();
                }

            }
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 构造相应实体
     * @return HttpServletResponse
     */
    @Override
    public WebServlet buildResponse() {
        HttpServletResponse response=new HttpServletResponse();
        response.setCode(200);
        response.setOutputStream(new ByteArrayOutputStream());
        response.addHeader("Server","LiteWeb V1.2 Beta");
        return response;
    }
}
