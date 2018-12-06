package com.bcd.nettyserver.http.config;

import com.bcd.nettyserver.http.NettyHttpServer;
import com.bcd.nettyserver.http.config.properties.NettyConfig;
import com.bcd.nettyserver.http.listener.NettyHttpTimeoutListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class NettyHttpServerBoot implements CommandLineRunner{
    private Logger logger= LoggerFactory.getLogger(NettyHttpServerBoot.class);
    @Autowired
    private NettyConfig nettyServerConfig;
    @Override
    public void run(String... args) throws Exception {
        nettyServerConfig.http.servers.forEach(e->{
            Executors.newSingleThreadExecutor().execute(()->new NettyHttpServer(e.id,e.port).run());
            logger.info("启动netty服务器["+e.port+"]!");
        });
        if(nettyServerConfig.http.servers.size()>0){
            //启动netty延时任务超时监听线程(每500ms扫描一次)
            Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new NettyHttpTimeoutListener(), 3000L, 500L, TimeUnit.MILLISECONDS);
            logger.info("启动netty延时任务超时监听线程!");
        }
    }
}
