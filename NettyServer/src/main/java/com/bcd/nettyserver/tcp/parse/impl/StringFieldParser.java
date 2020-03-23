package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.nettyserver.tcp.parse.FieldParser;
import io.netty.buffer.ByteBuf;

public class StringFieldParser implements FieldParser<String> {
    public final static StringFieldParser INSTANCE=new StringFieldParser();
    @Override
    public String parse(ByteBuf data,int len, Object ...ext) {
        int discardLen=0;
        byte[] bytes=new byte[len];
        data.readBytes(bytes);
        for(int i=bytes.length-1;i>=0;i--){
            if(bytes[i]==0){
                discardLen++;
            }else{
                break;
            }
        }
        return new String(bytes,0,bytes.length-discardLen);
    }
}
