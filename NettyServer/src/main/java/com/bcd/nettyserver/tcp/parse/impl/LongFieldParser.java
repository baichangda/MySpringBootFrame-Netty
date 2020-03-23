package com.bcd.nettyserver.tcp.parse.impl;


import com.bcd.nettyserver.tcp.parse.FieldParser;
import io.netty.buffer.ByteBuf;

public class LongFieldParser implements FieldParser<Long> {
    public final static LongFieldParser INSTANCE=new LongFieldParser();

    @Override
    public Long parse(ByteBuf data,int len, Object ...ext) {
        return toByteBuf(data,8,len).readLong();
    }

}
