package com.liteweb.anno;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface EntityParameter {
    Class<?> clazz();
}
