package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.nettyserver.tcp.info.FieldInfo;
import com.bcd.nettyserver.tcp.parse.FieldParseContext;
import com.bcd.nettyserver.tcp.parse.FieldParser;
import com.bcd.nettyserver.tcp.parse.FieldToHexContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

public class ByteBufFieldParser implements FieldParser<ByteBuf> {
    @Override
    public ByteBuf parse(ByteBuf data, int len, FieldParseContext context) {
        ByteBuf byteBuf= Unpooled.buffer(len,len);
        byteBuf.writeBytes(data,len);
        return byteBuf;
    }

    @Override
    public String toHex(ByteBuf data, int len, FieldToHexContext context) {
        checkHexData(data);
        return ByteBufUtil.hexDump(data);
    }
}
