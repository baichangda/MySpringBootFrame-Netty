package com.bcd.nettyserver.tcp;

import com.bcd.base.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component("tcpStarter")
public class Starter implements CommandLineRunner{
    Logger logger= LoggerFactory.getLogger(Starter.class);
    ExecutorService POOL;
    @Override
    public void run(String... args) throws Exception {
        Collection<TcpServer> tcpServers= SpringUtil.applicationContext.getBeansOfType(TcpServer.class).values();
        if(!tcpServers.isEmpty()) {
            POOL = Executors.newFixedThreadPool(tcpServers.size());
            tcpServers.forEach(tcpServer -> {
                POOL.execute(tcpServer);
                logger.info("启动netty tcp服务器[" + tcpServer.getPort() + "]!");
            });
        }
    }
}
