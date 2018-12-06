package com.bcd.nettyserver.http.anno;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NettyHttpRequestParam {
    String value();
    boolean required() default true;
}
