package com.liteweb.util;

import com.alibaba.fastjson.JSONObject;
import com.liteweb.entity.HttpServlet;
import com.liteweb.entity.HttpServletRequest;
import com.liteweb.entity.HttpServletResponse;
import com.liteweb.exception.ServerException;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {
    private static final Map<String,String> FILE_TYPE_TO_CONTENT_TYPE =new HashMap<>();
    private static final Map<String,String> FILE_TYPE_TO_DOWN_LOAD_TYPE =new HashMap<>();
    static {
        FILE_TYPE_TO_CONTENT_TYPE.put("css","text/css");
        FILE_TYPE_TO_CONTENT_TYPE.put("js","text/javascript");
        FILE_TYPE_TO_CONTENT_TYPE.put("png","image/png");
        FILE_TYPE_TO_CONTENT_TYPE.put("jpg","image/jpeg");
        FILE_TYPE_TO_DOWN_LOAD_TYPE.put("jar","");
        FILE_TYPE_TO_DOWN_LOAD_TYPE.put("zip","");
    }
    /**
     * 响应文件
     * @param filePath 文件路径，resource下
     * @return 比特数组输出流
     */
    public static ByteArrayOutputStream responseResourceFile(String filePath){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if(filePath.startsWith("/")){
            filePath=filePath.substring(1);
        }
        URL url=Thread.currentThread().getContextClassLoader().getResource(filePath);
        if(url==null){
            throw new ServerException("文件未找到");
        }
        String path=url.getPath();
        try {
            FileInputStream fileInputStream=new FileInputStream(path);
            realReadAndResponse(fileInputStream,outputStream);
        } catch (IOException e) {
            InputStream fileInputStream=Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
            try {
                realReadAndResponse(fileInputStream,outputStream);
            }catch (IOException eNot){
                eNot.printStackTrace();
            }
        }
        return outputStream;
    }

    private static void realReadAndResponse(InputStream inputStream,ByteArrayOutputStream outputStream) throws IOException{
        BufferedInputStream reader=new BufferedInputStream(inputStream);
        byte[] nowByte =new byte[512];
        int size;
        while ((size=reader.read(nowByte)) != -1){
            if(size!=512){
                outputStream.write(Arrays.copyOf(nowByte,size));
                break;
            }
            outputStream.write(nowByte);
            nowByte=new byte[512];
        }
    }

    /**
     * 返回JSON实体资源
     * @param o JSON实体
     * @return 比特数组输出流
     * @throws IOException 写入输出流异常
     */
    public static ByteArrayOutputStream responseResourceJson(Object o) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(JSONObject.toJSONString(o).getBytes(StandardCharsets.UTF_8));
        return outputStream;
    }

    /**
     * 输入流转文本
     * @param byteArrayInputStream 输入流
     * @return 文本结果
     * @throws IOException 读取异常
     */
    public static String byteArrayToString(ByteArrayInputStream byteArrayInputStream) throws IOException {
        InputStreamReader reader=new InputStreamReader(byteArrayInputStream);
        BufferedReader bufferedReader=new BufferedReader(reader);
        String res;
        StringBuilder stringBuilder=new StringBuilder();
        while((res=bufferedReader.readLine())!=null){
            stringBuilder.append(res);
        }
        return stringBuilder.toString();
    }

    public static String getFileContentType(String fileName){
        String contentType=FILE_TYPE_TO_CONTENT_TYPE.get(fileName);
        return contentType==null ? "text/"+fileName:contentType;
    }

    public static boolean isDownLoadFile(String fileName){
        return FILE_TYPE_TO_DOWN_LOAD_TYPE.get(fileName)!=null;
    }
}