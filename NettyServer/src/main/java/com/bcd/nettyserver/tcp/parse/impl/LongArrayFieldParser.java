package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.nettyserver.tcp.parse.FieldParser;
import io.netty.buffer.ByteBuf;


public class LongArrayFieldParser implements FieldParser<long[]> {
    public final static LongArrayFieldParser INSTANCE=new LongArrayFieldParser();
    @Override
    public long[] parse(ByteBuf data, int len, Object... ext) {
        int singleLen=(int)ext[0];
        long[] res=new long[len/singleLen];
        for(int i=0;i<=res.length-1;i++){
            res[i]=toByteBuf(data,8,singleLen).readLong();
        }
        return res;
    }
}
