package com.liteweb.anno;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JsonRest {
    String type() default "application/json";
}
