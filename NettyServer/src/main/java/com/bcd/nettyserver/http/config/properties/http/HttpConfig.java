package com.bcd.nettyserver.http.config.properties.http;

import java.util.List;

public class HttpConfig {
    public List<HttpServerConfig> servers;

    public List<HttpServerConfig> getServers() {
        return servers;
    }

    public void setServers(List<HttpServerConfig> servers) {
        this.servers = servers;
    }
}
