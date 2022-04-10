package com.liteweb.factory;

import com.liteweb.constant.ConfFileConstant;
import com.liteweb.util.PropertiesFileUtil;

import java.util.Properties;

public class PropertiesFactory {
    private static final Properties properties = PropertiesFileUtil.getProperties(ConfFileConstant.CONF_FILE_NAME);

    public static Properties createProperties() {
        return properties;
    }
}
