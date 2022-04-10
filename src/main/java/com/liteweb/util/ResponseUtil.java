package com.liteweb.util;

import com.liteweb.exception.ServerException;

import java.io.*;
import java.net.URL;

public class ResponseUtil {
    public static void responseResourceFile(ByteArrayOutputStream outputStream,String filePath){
        URL url=Thread.currentThread().getContextClassLoader().getResource(filePath);
        if(url==null){
            throw new ServerException("文件未找到");
        }
        String path=url.getPath();
        try {
            FileInputStream fileInputStream=new FileInputStream(path);
            BufferedInputStream reader=new BufferedInputStream(fileInputStream);
            byte[] now_byte=new byte[512];
            while (reader.read(now_byte) != -1){
                outputStream.write(now_byte);
                now_byte=new byte[512];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}