package com.liteweb.anno;

import java.lang.annotation.*;

/**
 * @author Hone
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StaticPageRest {
    String path();
    String type() default "text/html";
}