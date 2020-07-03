package com.bcd.nettyserver.tcp.parse.impl;


import com.bcd.nettyserver.tcp.parse.FieldParseContext;
import com.bcd.nettyserver.tcp.parse.FieldParser;
import com.bcd.nettyserver.tcp.parse.FieldToByteBufContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;


public class LongFieldParser implements FieldParser<Long> {
    public final static int BYTE_LENGTH=8;

    @Override
    public Long parse(ByteBuf data, int len, FieldParseContext context) {
        if(len==4){
            //优化处理 int->long
            return data.readUnsignedInt();
        }else {
            if (len == BYTE_LENGTH) {
                return data.readLong();
            } else if (len > BYTE_LENGTH) {
                data.skipBytes(len - BYTE_LENGTH);
                return data.readLong();
            } else {
                ByteBuf temp=Unpooled.buffer(BYTE_LENGTH,BYTE_LENGTH);
                temp.writeBytes(new byte[BYTE_LENGTH-len]);
                temp.writeBytes(data,len);
                return temp.readLong();
            }
        }
    }

    @Override
    public void toByteBuf(Long data, int len, FieldToByteBufContext context,ByteBuf result) {
        checkByteBufData(data);
        if(len==BYTE_LENGTH){
            result.writeLong(data);
        }else if(len>BYTE_LENGTH){
            result.writeBytes(new byte[len-BYTE_LENGTH]);
            result.writeLong(data);
        }else{
            for(int i=len;i>=1;i--){
                int move=8*(i-1);
                result.writeByte((byte)(data>>>move));
            }
        }
    }
}
