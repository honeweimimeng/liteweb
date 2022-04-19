package com.liteweb.scanner;

import com.liteweb.exception.ServerException;

public class SocketIoScanner {
    public void startIoScanner(SocketIoHandler socketIoHandler){
        while (true){
            try {
                socketIoHandler.invoke();
            }catch (Exception e){
                e.printStackTrace();
                throw new ServerException("SocketIO监听启动错误"+e.getMessage());
            }
        }
    }
    public void startIoScanner(){
        while (true){}
    }
}
