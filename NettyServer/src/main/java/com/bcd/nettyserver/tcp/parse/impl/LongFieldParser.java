package com.bcd.nettyserver.tcp.parse.impl;


import com.bcd.nettyserver.tcp.parse.FieldParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;


public class LongFieldParser implements FieldParser<Long> {
    public final static int BYTE_LENGTH=8;
    public final static LongFieldParser INSTANCE=new LongFieldParser();

    @Override
    public Long parse(ByteBuf data,int len, Object ...ext) {
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
    public String toHex(Long data, int len, Object... ext) {
        checkHexData(data);
        ByteBuf byteBuf= Unpooled.buffer(len,len);
        if(len==BYTE_LENGTH){
            byteBuf.writeLong(data);
        }else if(len>BYTE_LENGTH){
            byteBuf.writeBytes(new byte[len-BYTE_LENGTH]);
            byteBuf.writeLong(data);
        }else{
            for(int i=len;i>=1;i--){
                int move=8*(i-1);
                byteBuf.writeByte((byte)(data>>>move));
            }
        }
        return ByteBufUtil.hexDump(byteBuf);
    }
}
