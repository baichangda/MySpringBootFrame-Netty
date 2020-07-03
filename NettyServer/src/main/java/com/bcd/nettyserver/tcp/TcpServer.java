package com.bcd.nettyserver.tcp;

public abstract class TcpServer implements Runnable{
    protected int port;

    public TcpServer(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

}
