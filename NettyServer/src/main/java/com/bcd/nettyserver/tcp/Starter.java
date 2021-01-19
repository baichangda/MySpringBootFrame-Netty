package com.bcd.nettyserver.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component("tcpStarter")
public class Starter implements CommandLineRunner{

    @Autowired
    List<TcpServer> serverList;

    Logger logger= LoggerFactory.getLogger(Starter.class);
    ExecutorService POOL;
    @Override
    public void run(String... args) throws Exception {
        if(!serverList.isEmpty()) {
            POOL = Executors.newFixedThreadPool(serverList.size());
            serverList.forEach(tcpServer -> {
                POOL.execute(tcpServer);
            });
        }
    }
}
