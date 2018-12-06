package com.bcd.nettyserver.http.result.handler.impl;

import com.bcd.base.message.JsonMessage;
import com.bcd.base.util.ExceptionUtil;
import com.bcd.nettyserver.http.result.handler.NettyHttpResultHandler;
import com.bcd.nettyserver.http.util.NettyHttpUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;


public class NettyHttpResultHandlerImpl implements NettyHttpResultHandler {
    @Override
    public void handleResult(Object res, HttpRequest httpRequest, ChannelHandlerContext context) {
        if (res == null) {
            NettyHttpUtil.response(JsonMessage.success(), context, httpRequest.protocolVersion().isKeepAliveDefault());
        } else {
            if (res instanceof JsonMessage) {
                NettyHttpUtil.response((JsonMessage) res, context, httpRequest.protocolVersion().isKeepAliveDefault());
            } else if (res instanceof Throwable) {
                ExceptionUtil.printException((Throwable) res);
                NettyHttpUtil.response(ExceptionUtil.toJsonMessage((Throwable) res), context, httpRequest.protocolVersion().isKeepAliveDefault());
            } else {
                NettyHttpUtil.response(JsonMessage.success(res), context, httpRequest.protocolVersion().isKeepAliveDefault());
            }
        }
    }
}
