package com.liteweb.anno;

import java.lang.annotation.*;

/**
 * @author Hone
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServletConf {
    String path();
}