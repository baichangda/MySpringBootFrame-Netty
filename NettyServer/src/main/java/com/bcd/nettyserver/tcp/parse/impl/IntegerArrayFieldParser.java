package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.nettyserver.tcp.parse.FieldParser;
import io.netty.buffer.ByteBuf;


public class IntegerArrayFieldParser implements FieldParser<int[]> {
    public final static IntegerArrayFieldParser INSTANCE=new IntegerArrayFieldParser();
    @Override
    public int[] parse(ByteBuf data, int len, Object... ext) {
        int singleLen=(int)ext[0];
        int[] res=new int[len/singleLen];
        for(int i=0;i<=res.length-1;i++){
            res[i]=toByteBuf(data,4,singleLen).readInt();
        }
        return res;
    }
}
