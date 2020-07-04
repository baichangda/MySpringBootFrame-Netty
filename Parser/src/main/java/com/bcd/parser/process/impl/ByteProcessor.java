package com.bcd.parser.process.impl;


import com.bcd.parser.info.FieldInfo;
import com.bcd.parser.process.FieldDeProcessContext;
import com.bcd.parser.process.FieldProcessContext;
import com.bcd.parser.process.FieldProcessor;
import io.netty.buffer.ByteBuf;

import java.util.Objects;

public class ByteProcessor extends FieldProcessor {
    private final static int BYTE_LENGTH=1;
    @Override
    public boolean support(Class clazz) {
        return clazz==byte.class||clazz==Byte.class;
    }

    @Override
    public Object process(ByteBuf data, FieldProcessContext processContext) {
        int len=processContext.getLen();
        if(len==BYTE_LENGTH){
            return data.readByte();
        }else if(len>BYTE_LENGTH){
            data.skipBytes(len-BYTE_LENGTH);
            return data.readByte();
        }else{
            return 0;
        }
    }

    @Override
    public void deProcess(Object data, ByteBuf dest, FieldDeProcessContext processContext) {
        Objects.requireNonNull(data);
        int len=processContext.getLen();
        byte[] content=new byte[len];
        content[len-BYTE_LENGTH]=(byte)data;
        dest.writeBytes(content);
    }
}
