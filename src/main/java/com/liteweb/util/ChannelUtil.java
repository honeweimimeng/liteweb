package com.liteweb.util;

import com.liteweb.exception.ServerException;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;


/**
 * Channel操作工具类
 * @author Hone
 */
public class ChannelUtil {
    /**
     * 向管道写入数据
     * @param channel 管道
     * @param byteBuffer 缓冲区
     */
    public static void write(Channel channel, ByteBuffer byteBuffer) throws Exception{
        if(channel instanceof AsynchronousSocketChannel){
            AsynchronousSocketChannel asyChannel =(AsynchronousSocketChannel) channel;
            //等待返回结果
            asyChannel.write(byteBuffer).get();
        }else if(channel instanceof SocketChannel){
            SocketChannel synChannel =(SocketChannel) channel;
            synChannel.write(byteBuffer);
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
            AsynchronousSocketChannel asyChannel =(AsynchronousSocketChannel) channel;
            //等待返回结果
            asyChannel.read(byteBuffer).get();
        }else if(channel instanceof SocketChannel){
            SocketChannel synChannel =(SocketChannel) channel;
            synChannel.read(byteBuffer);
        }else{
            channel.close();
            throw new ServerException("读取握手消息失败");
        }
    }
}
