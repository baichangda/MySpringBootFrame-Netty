package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.nettyserver.tcp.parse.FieldParser;
import io.netty.buffer.ByteBuf;


public class ShortArrayFieldParser implements FieldParser<short[]> {
    public final static ShortArrayFieldParser INSTANCE=new ShortArrayFieldParser();

    @Override
    public short[] parse(ByteBuf data, int len, Object... ext) {
        int singleLen=(int)ext[0];
        short[] res=new short[len/singleLen];
        for(int i=0;i<=res.length-1;i++){
            res[i]=toByteBuf(data,2,singleLen).readShort();
        }
        return res;
    }
}
