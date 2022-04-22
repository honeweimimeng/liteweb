package com.liteweb.entity;

import com.liteweb.connector.ServletConnector;
import java.nio.ByteBuffer;

/**
 * @author Hone
 */
public interface WebServlet {

    /**
     * 消息缓冲区列表
     * @return ByteBuffer[]
     */
    ByteBuffer[] messageBuffers();
}
