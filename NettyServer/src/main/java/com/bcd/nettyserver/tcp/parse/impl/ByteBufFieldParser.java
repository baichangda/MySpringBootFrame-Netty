package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.nettyserver.tcp.parse.FieldParseContext;
import com.bcd.nettyserver.tcp.parse.FieldParser;
import com.bcd.nettyserver.tcp.parse.FieldToByteBufContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ByteBufFieldParser implements FieldParser<ByteBuf> {
    @Override
    public ByteBuf parse(ByteBuf data, int len, FieldParseContext context) {
        ByteBuf byteBuf= Unpooled.buffer(len,len);
        byteBuf.writeBytes(data,len);
        return byteBuf;
    }

    @Override
    public ByteBuf toByteBuf(ByteBuf data, int len, FieldToByteBufContext context) {
        checkByteBufData(data);
        return data;
    }
}
