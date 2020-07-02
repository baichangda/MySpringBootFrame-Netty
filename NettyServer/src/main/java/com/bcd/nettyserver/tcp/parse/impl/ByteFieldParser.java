package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.nettyserver.tcp.parse.FieldParseContext;
import com.bcd.nettyserver.tcp.parse.FieldParser;
import com.bcd.nettyserver.tcp.parse.FieldToByteBufContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;


public class ByteFieldParser implements FieldParser<Byte> {
    public final static int BYTE_LENGTH=1;
    @Override
    public Byte parse(ByteBuf data,int len, FieldParseContext context) {
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
    public ByteBuf toByteBuf(Byte data, int len, FieldToByteBufContext context) {
        checkByteBufData(data);
        byte[] content=new byte[len];
        content[len-BYTE_LENGTH]=data;
        return Unpooled.wrappedBuffer(content);
    }
}
