package com.bcd.parser.process.impl;

import com.bcd.parser.process.FieldDeProcessContext;
import com.bcd.parser.process.FieldProcessContext;
import com.bcd.parser.process.FieldProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Objects;

public class IntegerProcessor extends FieldProcessor<Integer> {
    public final static int BYTE_LENGTH=4;

    @Override
    public Integer process(ByteBuf data, FieldProcessContext processContext){
        int len=processContext.getLen();
        if(len==2){
            //优化处理 short->int
            return (int)withValExpr(data.readUnsignedShort(),processContext);
        }else {
            if (len == BYTE_LENGTH) {
                return (int)withValExpr(data.readInt(),processContext);
            } else if (len > BYTE_LENGTH) {
                data.skipBytes(len - BYTE_LENGTH);
                return (int)withValExpr(data.readInt(),processContext);
            } else {
                ByteBuf temp= Unpooled.buffer(BYTE_LENGTH,BYTE_LENGTH);
                temp.writeBytes(new byte[BYTE_LENGTH-len]);
                temp.writeBytes(data,len);
                return (int)withValExpr(temp.readInt(),processContext);
            }
        }
    }

    @Override
    public void deProcess(Integer data, ByteBuf dest, FieldDeProcessContext processContext) {
        Objects.requireNonNull(data);
        checkValRpnNull(processContext);
        int len=processContext.getLen();
        if(len==BYTE_LENGTH){
            dest.writeInt(data);
        }else if(len>BYTE_LENGTH){
            dest.writeBytes(new byte[len-BYTE_LENGTH]);
            dest.writeInt(data);
        }else{
            for(int i=len;i>=1;i--){
                int move=8*(i-1);
                dest.writeByte((byte)(data>>>move));
            }
        }
    }
}
