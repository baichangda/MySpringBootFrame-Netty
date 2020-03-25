package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.base.exception.BaseRuntimeException;
import com.bcd.nettyserver.tcp.parse.FieldParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Objects;

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

    @Override
    public String toHex(String data, int len, Object... ext) {
        checkHexData(data);
        ByteBuf byteBuf= Unpooled.buffer(len,len);
        byte[] content=data.getBytes();
        if(content.length==len){
            byteBuf.writeBytes(content);
        }else if(content.length<len){
            byteBuf.writeBytes(content);
            byteBuf.writeBytes(new byte[len-content.length]);
        }else{
            throw BaseRuntimeException.getException("toHex error,data byte length["+content.length+"]>len["+len+"]");
        }
        return null;
    }
}
