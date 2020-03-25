package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.nettyserver.tcp.parse.FieldParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;



public class ByteFieldParser implements FieldParser<Byte>{
    public final static int BYTE_LENGTH=1;
    public final static ByteFieldParser INSTANCE=new ByteFieldParser();
    @Override
    public Byte parse(ByteBuf data,int len, Object ...ext) {
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
    public String toHex(Byte data, int len, Object... ext) {
        checkHexData(data);
        byte[] content=new byte[len];
        content[len-BYTE_LENGTH]=data;
        return ByteBufUtil.hexDump(content);
    }
}
