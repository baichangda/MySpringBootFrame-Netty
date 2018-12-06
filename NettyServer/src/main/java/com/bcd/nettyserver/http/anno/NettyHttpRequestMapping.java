package com.bcd.nettyserver.http.anno;



import com.bcd.nettyserver.http.define.NettyHttpRequestMethodEnum;

import java.lang.annotation.*;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NettyHttpRequestMapping {
    String value();
    long timeout() default -1l;
    NettyHttpRequestMethodEnum method() default NettyHttpRequestMethodEnum.GET;
}
