package com.bcd.nettyserver.tcp.parse.impl;


import com.bcd.nettyserver.tcp.parse.FieldParser;
import io.netty.buffer.ByteBuf;

public class IntegerFieldParser implements FieldParser<Integer> {
    public final static IntegerFieldParser INSTANCE=new IntegerFieldParser();

    @Override
    public Integer parse(ByteBuf data,int len, Object ...ext) {
        return toByteBuf(data,4,len).readInt();
    }


}
