package com.liteweb.util;

import com.liteweb.exception.ServerException;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;


/**
 * Channel操作工具类
 */
public class ChannelUtil {
    /**
     * 向管道写入数据
     * @param channel 管道
     * @param byteBuffer 缓冲区
     */
    public static void write(Channel channel, ByteBuffer byteBuffer) throws Exception{
        if(channel instanceof AsynchronousSocketChannel){
            AsynchronousSocketChannel asy_channel=(AsynchronousSocketChannel) channel;
            //等待返回结果
            asy_channel.write(byteBuffer).get();
        }else if(channel instanceof SocketChannel){
            SocketChannel syn_channel=(SocketChannel) channel;
            syn_channel.write(byteBuffer);
        }else{
            channel.close();
            throw new ServerException("读取握手消息失败");
        }
    }

    /**
     * 向管道读取数据
     * @param channel 管道
     * @param byteBuffer 缓冲区
     */
    public static void read(Channel channel, ByteBuffer byteBuffer) throws Exception{
        if(channel instanceof AsynchronousSocketChannel){
            AsynchronousSocketChannel asy_channel=(AsynchronousSocketChannel) channel;
            //等待返回结果
            asy_channel.read(byteBuffer).get();
        }else if(channel instanceof SocketChannel){
            SocketChannel syn_channel=(SocketChannel) channel;
            syn_channel.read(byteBuffer);
        }else{
            channel.close();
            throw new ServerException("读取握手消息失败");
        }
    }
}
