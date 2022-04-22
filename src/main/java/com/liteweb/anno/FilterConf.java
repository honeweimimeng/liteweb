package com.liteweb.anno;

import java.lang.annotation.*;

/**
 * @author Hone
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FilterConf {
    String path() default "/*";
}
