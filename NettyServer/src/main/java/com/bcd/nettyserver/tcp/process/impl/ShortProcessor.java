package com.bcd.nettyserver.tcp.process.impl;

import com.bcd.nettyserver.tcp.info.FieldInfo;
import com.bcd.nettyserver.tcp.process.FieldProcessContext;
import com.bcd.nettyserver.tcp.process.FieldProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Objects;

public class ShortProcessor extends FieldProcessor {
    private final static int BYTE_LENGTH=2;

    @Override
    public boolean support(Class clazz) {
        return clazz==short.class||clazz==Short.class;
    }

    @Override
    public Object process(ByteBuf data, Object instance, FieldProcessContext processContext) {
        int len=processContext.getLen();
        if(len==1){
            //优化处理 byte->short
            return data.readUnsignedByte();
        }else {
            if (len == BYTE_LENGTH) {
                return data.readShort();
            } else if (len > BYTE_LENGTH) {
                data.skipBytes(len - BYTE_LENGTH);
                return data.readShort();
            } else {
                ByteBuf temp= Unpooled.buffer(BYTE_LENGTH,BYTE_LENGTH);
                temp.writeBytes(new byte[BYTE_LENGTH-len]);
                temp.writeBytes(data,len);
                return temp.readShort();
            }
        }
    }

    @Override
    public void deProcess(Object data, ByteBuf dest, FieldProcessContext processContext) {
        Objects.requireNonNull(data);
        int len=processContext.getLen();
        if(len==BYTE_LENGTH){
            dest.writeShort((short)data);
        }else if(len>BYTE_LENGTH){
            dest.writeBytes(new byte[len-BYTE_LENGTH]);
            dest.writeShort((short)data);
        }else{
            for(int i=len;i>=1;i--){
                int move=8*(i-1);
                dest.writeByte((byte)((short)data>>>move));
            }
        }
    }
}
