package com.bcd.nettyserver.http;

import com.bcd.nettyserver.http.listener.NettyHttpTimeoutListener;
import com.bcd.nettyserver.http.properties.NettyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
@ConditionalOnProperty("netty.http.id")
@Component("httpStarter")
public class Starter implements CommandLineRunner{
    private Logger logger= LoggerFactory.getLogger(Starter.class);
    @Autowired
    private NettyConfig nettyConfig;
    @Override
    public void run(String... args) throws Exception {
        Executors.newSingleThreadExecutor().execute(new HttpServer(nettyConfig.http.id,nettyConfig.http.port));
        logger.info("启动netty http服务器["+nettyConfig.http.port+"]!");
        //启动netty延时任务超时监听线程(每500ms扫描一次)
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new NettyHttpTimeoutListener(), 3000L, 500L, TimeUnit.MILLISECONDS);
        logger.info("启动netty http延时任务超时监听线程!");
    }
}
