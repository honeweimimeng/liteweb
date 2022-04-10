package com.liteweb.container;

import com.liteweb.entity.WebFilter;
import com.liteweb.entity.WebServlet;

public interface ServletContainer{
    void disposeMessage(WebServlet request, WebServlet response , WebFilter filter);
}