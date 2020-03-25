package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.nettyserver.tcp.parse.FieldParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;


public class ShortFieldParser implements FieldParser<Short> {
    public final static int BYTE_LENGTH=2;
    public final static ShortFieldParser INSTANCE=new ShortFieldParser();

    @Override
    public Short parse(ByteBuf data,int len, Object ...ext) {
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
    public String toHex(Short data, int len, Object... ext) {
        checkHexData(data);
        ByteBuf byteBuf= Unpooled.buffer(len,len);
        if(len==BYTE_LENGTH){
            byteBuf.writeShort(data);
        }else if(len>BYTE_LENGTH){
            byteBuf.writeBytes(new byte[len-BYTE_LENGTH]);
            byteBuf.writeShort(data);
        }else{
            for(int i=len;i>=1;i--){
                int move=8*(i-1);
                byteBuf.writeByte((byte)(data>>>move));
            }
        }
        return ByteBufUtil.hexDump(byteBuf);
    }
}
