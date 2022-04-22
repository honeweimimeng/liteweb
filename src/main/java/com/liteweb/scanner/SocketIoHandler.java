package com.liteweb.scanner;

/**
 * @author Hone
 */
public interface SocketIoHandler {
    /**
     * SocketIo轮询执行方法
     * @throws Exception 轮询异常
     */
    void invoke() throws Exception;
}
