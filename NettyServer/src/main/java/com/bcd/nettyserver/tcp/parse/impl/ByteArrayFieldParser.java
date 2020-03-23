package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.nettyserver.tcp.parse.FieldParser;
import io.netty.buffer.ByteBuf;


public class ByteArrayFieldParser implements FieldParser<byte[]> {
    public final static ByteArrayFieldParser INSTANCE=new ByteArrayFieldParser();
    @Override
    public byte[] parse(ByteBuf data, int len, Object... ext) {
        int singleLen=(int)ext[0];
        byte[] res=new byte[len/singleLen];
        for(int i=0;i<=res.length-1;i++){
            res[i]=toByteBuf(data,1,singleLen).readByte();
        }
        return res;
    }
}
