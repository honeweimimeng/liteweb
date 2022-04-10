package com.liteweb.entity;

import com.liteweb.connector.ServletConnector;
import java.nio.ByteBuffer;

public interface WebServlet {
    ByteBuffer[] messageBuffers();
}
