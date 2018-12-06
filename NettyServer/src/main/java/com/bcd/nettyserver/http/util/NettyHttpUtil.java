package com.bcd.nettyserver.http.util;

import com.bcd.base.exception.BaseRuntimeException;
import com.bcd.base.message.JsonMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class NettyHttpUtil {
    public static void response(JsonMessage res, ChannelHandlerContext ctx, boolean isKeepAlive) {
        try {
            byte[] datas = new ObjectMapper().writeValueAsBytes(res);
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(datas));
            response.headers().set("Content-Type", "application/json;charset=UTF-8");
            response.headers().set("Content-Length", Integer.toString(datas.length));
            if (!isKeepAlive) {
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set("Connection", "keep-alive");
                ctx.writeAndFlush(response);
            }
        } catch (JsonProcessingException e) {
            throw BaseRuntimeException.getException(e);
        }
    }
}
