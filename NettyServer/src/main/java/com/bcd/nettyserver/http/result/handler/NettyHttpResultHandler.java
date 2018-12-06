package com.bcd.nettyserver.http.result.handler;


import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public interface NettyHttpResultHandler {
    void handleResult(Object res, HttpRequest httpRequest, ChannelHandlerContext context);
}