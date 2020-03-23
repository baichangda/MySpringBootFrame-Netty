package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.nettyserver.tcp.parse.FieldParser;
import io.netty.buffer.ByteBuf;


public class ByteFieldParser implements FieldParser<Byte>{
    public final static ByteFieldParser INSTANCE=new ByteFieldParser();
    @Override
    public Byte parse(ByteBuf data,int len, Object ...ext) {
        return toByteBuf(data,1,len).readByte();
    }
}
