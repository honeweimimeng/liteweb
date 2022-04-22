package com.liteweb.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Hone
 */
public class ThreadPoolFactory implements ThreadFactory {
    private final String namePrefix;
    private final ThreadGroup threadGroup;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);

    public ThreadPoolFactory(String tag){
        SecurityManager s = System.getSecurityManager();
        threadGroup = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        if (null == tag || "".equals(tag.trim())) {
            tag = "pool";
        }
        namePrefix = tag + "-" +
                POOL_NUMBER.getAndIncrement() +
                "-thread-";
    }
    @Override
    public Thread newThread(Runnable r) {
        return new Thread(threadGroup,r,namePrefix + threadNumber.getAndIncrement(),
                0);
    }
}
