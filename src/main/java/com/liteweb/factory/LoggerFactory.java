package com.liteweb.factory;

import com.liteweb.formatter.LogFormatter;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * Logger方法工厂
 * @author Hone
 */
public class LoggerFactory {

    /**
     * 建造单个logger对象
     * @param msg
     * Logger主题
     * @return Logger
     */
    public static Logger createInfo(String msg){
        Logger getLogger=Logger.getLogger(msg);
        getLogger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        LogFormatter formatter = new LogFormatter();
        formatter.RestColor(LogFormatter.ANSI_WHITE);
        handler.setFormatter(formatter);
        getLogger.addHandler(handler);
        return getLogger;
    }

    /**
     * 建造单个logger异常对象
     * @param msg
     * Logger主题
     * @return Logger
     */
    public static Logger createException(String msg){
        Logger getLogger=Logger.getLogger(msg);
        getLogger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        LogFormatter formatter = new LogFormatter();
        formatter.RestColor(LogFormatter.ANSI_RED);
        handler.setFormatter(formatter);
        getLogger.addHandler(handler);
        return getLogger;
    }

    /**
     * 建造单个logger警告对象
     * @param msg
     * Logger主题
     * @return Logger
     */
    public static Logger createWarning(String msg){
        Logger getLogger=Logger.getLogger(msg);
        getLogger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        LogFormatter formatter = new LogFormatter();
        formatter.RestColor(LogFormatter.ANSI_YELLOW);
        handler.setFormatter(formatter);
        getLogger.addHandler(handler);
        return getLogger;
    }
}
