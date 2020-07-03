package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.nettyserver.tcp.parse.FieldParseContext;
import com.bcd.nettyserver.tcp.parse.FieldParser;
import com.bcd.nettyserver.tcp.parse.FieldToByteBufContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;


public class ShortFieldParser implements FieldParser<Short> {
    public final static int BYTE_LENGTH=2;

    @Override
    public Short parse(ByteBuf data,int len, FieldParseContext context) {
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
                ByteBuf temp=Unpooled.buffer(BYTE_LENGTH,BYTE_LENGTH);
                temp.writeBytes(new byte[BYTE_LENGTH-len]);
                temp.writeBytes(data,len);
                return temp.readShort();
            }
        }
    }

    @Override
    public void toByteBuf(Short data, int len, FieldToByteBufContext context,ByteBuf result) {
        checkByteBufData(data);
        if(len==BYTE_LENGTH){
            result.writeShort(data);
        }else if(len>BYTE_LENGTH){
            result.writeBytes(new byte[len-BYTE_LENGTH]);
            result.writeShort(data);
        }else{
            for(int i=len;i>=1;i--){
                int move=8*(i-1);
                result.writeByte((byte)(data>>>move));
            }
        }
    }
}
