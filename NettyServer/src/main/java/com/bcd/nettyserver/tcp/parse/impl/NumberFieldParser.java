package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.nettyserver.tcp.parse.FieldParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public abstract class NumberFieldParser<T> implements FieldParser<T> {
    private int byteLen;
    public NumberFieldParser(int byteLen) {
        this.byteLen=byteLen;
    }

    public ByteBuf toByteBuf(byte[] data,Object ...ext){
        int len=data.length;
        ByteBuf byteBuf= Unpooled.buffer();
        if(len<byteLen){
            byte[] appendArr=new byte[byteLen-data.length];
            for(int i=0;i<=appendArr.length-1;i++){
                appendArr[i]=0;
            }
            byteBuf.writeBytes(appendArr);
            byteBuf.writeBytes(data);
        }else{
            byteBuf.writeBytes(data);
        }
        return byteBuf;
    }
}
