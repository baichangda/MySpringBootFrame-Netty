package com.bcd.nettyserver.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TcpServer implements Runnable{
    protected Logger logger= LoggerFactory.getLogger(this.getClass());

    protected int port;

    public TcpServer(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

}
