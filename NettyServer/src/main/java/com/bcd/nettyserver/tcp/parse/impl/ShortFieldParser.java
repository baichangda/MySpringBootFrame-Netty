package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.nettyserver.tcp.parse.FieldParser;
import io.netty.buffer.ByteBuf;

public class ShortFieldParser implements FieldParser<Short> {
    public final static ShortFieldParser INSTANCE=new ShortFieldParser();

    @Override
    public Short parse(ByteBuf data,int len, Object ...ext) {
        return toByteBuf(data,2,len).readShort();
    }
}
