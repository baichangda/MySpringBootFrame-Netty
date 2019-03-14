package com.bcd.nettyserver.http.config.properties.http;


public class HttpConfig {
    public String id;
    public int port;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
