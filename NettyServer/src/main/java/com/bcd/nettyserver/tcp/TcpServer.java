package com.bcd.nettyserver.tcp;

import com.bcd.nettyserver.tcp.parse.Parser;

public abstract class TcpServer implements Runnable{
    protected int port;

    public TcpServer(int port, Parser parser) {
        this.port = port;
    }

    public TcpServer(int port) {
        this(port,new Parser());
    }

    public int getPort() {
        return port;
    }

}
