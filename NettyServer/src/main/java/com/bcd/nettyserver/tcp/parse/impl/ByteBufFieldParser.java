package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.nettyserver.tcp.parse.FieldParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

public class ByteBufFieldParser implements FieldParser<ByteBuf> {
    public final static ByteBufFieldParser INSTANCE=new ByteBufFieldParser();
    @Override
    public ByteBuf parse(ByteBuf data, int len, Object... ext) {
        ByteBuf byteBuf= Unpooled.buffer(len,len);
        byteBuf.writeBytes(data,len);
        return byteBuf;
    }

    @Override
    public String toHex(ByteBuf data, int len, Object... ext) {
        checkHexData(data);
        return ByteBufUtil.hexDump(data);
    }
}
