package com.bcd.nettyserver.http.anno;

import java.lang.annotation.*;

/**
 * 使用此注解标注的controller会被加载到spring容器中;同时netty服务器接收到的请求会转发到这些controller上面
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NettyHttpController {
    /**
     * 服务器标识符
     * @return
     */
    String value();
}
