package com.bcd.nettyserver.tcp.parse.impl;


import com.bcd.nettyserver.tcp.info.FieldInfo;
import com.bcd.nettyserver.tcp.parse.FieldParseContext;
import com.bcd.nettyserver.tcp.parse.FieldParser;
import com.bcd.nettyserver.tcp.parse.FieldToHexContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
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
    public String toHex(Integer data, int len, FieldToHexContext context) {
        checkHexData(data);
        ByteBuf byteBuf= Unpooled.buffer(len,len);
        if(len==BYTE_LENGTH){
            byteBuf.writeInt(data);
        }else if(len>BYTE_LENGTH){
            byteBuf.writeBytes(new byte[len-BYTE_LENGTH]);
            byteBuf.writeInt(data);
        }else{
            for(int i=len;i>=1;i--){
                int move=8*(i-1);
                byteBuf.writeByte((byte)(data>>>move));
            }
        }
        return ByteBufUtil.hexDump(byteBuf);
    }
}
