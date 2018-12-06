package com.bcd.nettyserver.http.data;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class NettyHttpRequestData {
    private String id;
    private HttpRequest request;
    private ChannelHandlerContext context;

    public NettyHttpRequestData(String id, HttpRequest request, ChannelHandlerContext context) {
        this.id = id;
        this.request = request;
        this.context = context;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public void setContext(ChannelHandlerContext context) {
        this.context = context;
    }
}
