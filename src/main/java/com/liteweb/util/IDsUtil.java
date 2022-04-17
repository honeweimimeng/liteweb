package com.liteweb.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class IDsUtil {

    /**
     * 无锁式取唯一ID，也有可能冲突，在大尺度不冲突
     * @param name 标识字符
     * @return 唯一ID字符
     */
    public static String getId(String name){
        Random random=ThreadLocalRandom.current();
        return ((System.nanoTime()<<random.nextInt(16))&System.nanoTime())+name;
    }
}