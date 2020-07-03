package com.bcd.nettyserver.tcp.process.impl;

import com.bcd.base.exception.BaseRuntimeException;
import com.bcd.nettyserver.tcp.process.FieldProcessContext;
import com.bcd.nettyserver.tcp.process.FieldProcessor;
import io.netty.buffer.ByteBuf;

import java.util.Objects;

public class StringProcessor extends FieldProcessor<String> {
    @Override
    public boolean support(Class clazz) {
        return clazz==String.class;
    }

    @Override
    public String process(ByteBuf data, Object instance, FieldProcessContext processContext) {
        int discardLen=0;
        int len =processContext.getLen();
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
    public void deProcess(String data, ByteBuf dest, FieldProcessContext processContext) {
        Objects.requireNonNull(data);
        int len=processContext.getLen();
        byte[] content=data.getBytes();
        if(content.length==len){
            dest.writeBytes(content);
        }else if(content.length<len){
            dest.writeBytes(content);
            dest.writeBytes(new byte[len-content.length]);
        }else{
            throw BaseRuntimeException.getException("toByteBuf error,data byte length["+content.length+"]>len["+len+"]");
        }
    }
}
