package com.bcd.nettyserver.http.config.properties;

import com.bcd.nettyserver.http.config.properties.http.HttpConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "netty")
public class NettyConfig {
    public HttpConfig http;

    public HttpConfig getHttp() {
        return http;
    }

    public void setHttp(HttpConfig http) {
        this.http = http;
    }
}
