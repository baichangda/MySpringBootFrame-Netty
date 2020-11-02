package com.bcd.parser.process.impl;

import com.bcd.parser.process.FieldDeProcessContext;
import com.bcd.parser.process.FieldProcessContext;
import com.bcd.parser.process.FieldProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Objects;

public class LongProcessor extends FieldProcessor<Long> {
    public final static int BYTE_LENGTH=8;

    @Override
    public Long process(ByteBuf data, FieldProcessContext processContext){
        int len=processContext.getLen();
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
                ByteBuf temp= Unpooled.buffer(BYTE_LENGTH,BYTE_LENGTH);
                temp.writeBytes(new byte[BYTE_LENGTH-len]);
                temp.writeBytes(data,len);
                return temp.readLong();
            }
        }
    }

    @Override
    public void deProcess(Long data, ByteBuf dest, FieldDeProcessContext processContext) {
        Objects.requireNonNull(data);
        int len=processContext.getLen();
        if(len==BYTE_LENGTH){
            dest.writeLong(data);
        }else if(len>BYTE_LENGTH){
            dest.writeBytes(new byte[len-BYTE_LENGTH]);
            dest.writeLong(data);
        }else{
            for(int i=len;i>=1;i--){
                int move=8*(i-1);
                dest.writeByte((byte)(data>>>move));
            }
        }
    }
}
