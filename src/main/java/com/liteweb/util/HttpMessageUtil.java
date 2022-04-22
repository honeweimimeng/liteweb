package com.liteweb.util;

import com.liteweb.constant.RunTimeConstant;
import com.liteweb.constant.ServerConstant;
import com.liteweb.entity.CookieContext;
import com.liteweb.entity.HttpServletRequest;
import com.liteweb.enums.HttpStatusCode;

import java.util.*;

/**
 * @author Hone
 */
public class HttpMessageUtil {

    /**
     * 设置request实体的的首行属性
     * @param filed
     * 首行文本
     * @param request
     * 请求报文实体
     */
    public static void setHeaderFirstLine(String filed, HttpServletRequest request){
        String doubleNull = RunTimeConstant.NULL_STR+RunTimeConstant.NULL_STR;
        while(filed.contains(doubleNull)){
            filed=filed.replace(doubleNull,RunTimeConstant.NULL_STR);
        }
        String[] firstLineStrArr=filed.split(RunTimeConstant.NULL_STR);
        if(firstLineStrArr.length<3){
            throw new RuntimeException("错误的请求报文头或方式");
        }
        request.setMethod(firstLineStrArr[0]);
        String uriFull=firstLineStrArr[1];
        int tag;
        if((tag=uriFull.indexOf(ServerConstant.HTTP_URL_PRAGMAS_TAG))>0){
            if(tag<uriFull.length()-1){
                String pragma=uriFull.substring(tag+1);
                request.setPragma(HttpMessageUtil.urlSplit(pragma));
            }
            uriFull=uriFull.substring(0,tag);
        }
        request.setRequestURI(uriFull);
        request.setProtocol(firstLineStrArr[2]);
    }

    /**
     * 设置非首行的属性K,V
     * @param filed
     * 行文本
     * @param request
     * 请求实体
     */
    public static void setOtherFiled(String filed, HttpServletRequest request){
        filed=filed.replace(RunTimeConstant.NULL_STR,"");
        int spliceIndex=filed.indexOf(ServerConstant.HTTP_URL_FILED_SPLICE);
        String key=filed.substring(0,spliceIndex);
        String value=filed.substring(spliceIndex+1);
        if("user-agent".equalsIgnoreCase(key)){
            request.setUserAgent(value);
        }
        if("accept".equalsIgnoreCase(key)){
            request.setAccept(value);
        }
        if("accept-encoding".equalsIgnoreCase(key)){
            request.setAcceptEncoding(value);
        }
        if("accept-language".equalsIgnoreCase(key)){
            request.setAcceptLanguage(value);
        }
        if("connection".equalsIgnoreCase(key)){
            request.setConnection(value);
        }
        if("content-type".equalsIgnoreCase(key)){
            request.setContentType(value);
        }
        if("content-length".equalsIgnoreCase(key)){
            request.setContentLength(Integer.parseInt(value));
        }
        if("Host".equalsIgnoreCase(key)){
            request.setHost(value);
        }
        if("referer".equalsIgnoreCase(key)){
            request.setReferer(value);
        }
        if("cookie".equalsIgnoreCase(key)){
            request.setCookieContext(cookieSplit(value,request.getHost()));
        }
    }

    /**
     * 获取UEL参数MAP
     * @param strUrlParam
     * 参数字符串
     * @return Map<String, String>
     * 参数Map实体
     */
    public static Map<String, String> urlSplit(String strUrlParam){
        Map<String, String> mapRequest = new HashMap<String, String>();
        String[] arrSplit;
        arrSplit=strUrlParam.split("&");
        Arrays.stream(arrSplit).forEach((strSplit)->{
            String[] arrSplitEqual=null;
            arrSplitEqual= strSplit.split("=");
            //解析出键值
            if(arrSplitEqual.length>1){
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            }else{
                if(!Objects.equals(arrSplitEqual[0], "")){
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        });
        return mapRequest;
    }

    /**
     * 裁剪cookie
     * @param cookies cookies字符串
     * @param host 主机
     * @return CookieContext上下文实体
     */
    public static CookieContext cookieSplit(String cookies,String host){
        CookieContext cookieContext=new CookieContext(host);
        if(cookies!=null&&!cookies.isEmpty()){
            String[] cookieItem =cookies.split(";");
            if(!cookies.contains(";")){
                cookieItem= new String[]{cookies};
            }
            Arrays.stream(cookieItem).forEach(item->{
                int index=cookies.indexOf("=");
                cookieContext.addCookie(cookies.substring(0,index),
                        cookies.substring(index+1));
            });
        }
        return cookieContext;
    }

    /**
     * 获取报文头字符串
     * @param header 报文头参数
     * @param code 状态码
     * @return 报文头
     */
    public static String setResponseHeader(Map<String,List<String>> header,int code){
        StringBuilder stringBuilder = new StringBuilder()
                .append(ServerConstant.HTTP_SERVER_VERSION)
                .append(RunTimeConstant.NULL_STR)
                .append(code)
                .append(RunTimeConstant.NULL_STR)
                .append(HttpStatusCode.getTips(code))
                .append(ServerConstant.HTTP_LINE_TAG);
        header.keySet().forEach(item->{
            List<String> values=header.get(item);
            values.forEach(value->{
                stringBuilder.append(item).append(ServerConstant.HTTP_URL_FILED_SPLICE)
                        .append(RunTimeConstant.NULL_STR)
                        .append(value).append(ServerConstant.HTTP_LINE_TAG);
            });
        });
        stringBuilder.append(ServerConstant.HTTP_LINE_TAG);
        return stringBuilder.toString();
    }
}