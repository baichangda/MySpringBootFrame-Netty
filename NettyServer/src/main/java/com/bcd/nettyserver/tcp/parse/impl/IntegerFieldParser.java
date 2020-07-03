package com.bcd.nettyserver.tcp.parse.impl;


import com.bcd.nettyserver.tcp.parse.FieldParseContext;
import com.bcd.nettyserver.tcp.parse.FieldParser;
import com.bcd.nettyserver.tcp.parse.FieldToByteBufContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;


public class IntegerFieldParser implements FieldParser<Integer> {
    public final static int BYTE_LENGTH=4;

    @Override
    public Integer parse(ByteBuf data, int len, FieldParseContext context) {
        if(len==2){
            //优化处理 short->int
            return data.readUnsignedShort();
        }else {
            if (len == BYTE_LENGTH) {
                return data.readInt();
            } else if (len > BYTE_LENGTH) {
                data.skipBytes(len - BYTE_LENGTH);
                return data.readInt();
            } else {
                ByteBuf temp=Unpooled.buffer(BYTE_LENGTH,BYTE_LENGTH);
                temp.writeBytes(new byte[BYTE_LENGTH-len]);
                temp.writeBytes(data,len);
                return temp.readInt();
            }
        }
    }

    @Override
    public void toByteBuf(Integer data, int len, FieldToByteBufContext context,ByteBuf result) {
        checkByteBufData(data);
        if(len==BYTE_LENGTH){
            result.writeInt(data);
        }else if(len>BYTE_LENGTH){
            result.writeBytes(new byte[len-BYTE_LENGTH]);
            result.writeInt(data);
        }else{
            for(int i=len;i>=1;i--){
                int move=8*(i-1);
                result.writeByte((byte)(data>>>move));
            }
        }
    }
}
