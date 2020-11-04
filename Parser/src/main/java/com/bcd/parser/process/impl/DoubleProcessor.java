package com.bcd.parser.process.impl;

import com.bcd.parser.process.FieldDeProcessContext;
import com.bcd.parser.process.FieldProcessContext;
import com.bcd.parser.process.FieldProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Objects;

public class DoubleProcessor extends FieldProcessor<Double> {
    public final static int BYTE_LENGTH=8;

    @Override
    public Double process(ByteBuf data, FieldProcessContext processContext) {
        int len=processContext.getLen();
        if(len==4){
            //优化处理 int->long
            return withValExpr(data.readUnsignedInt(),processContext);
        }else {
            if (len == BYTE_LENGTH) {
                return withValExpr(data.readLong(),processContext);
            } else if (len > BYTE_LENGTH) {
                data.skipBytes(len - BYTE_LENGTH);
                return withValExpr(data.readLong(),processContext);
            } else {
                ByteBuf temp= Unpooled.buffer(BYTE_LENGTH,BYTE_LENGTH);
                temp.writeBytes(new byte[BYTE_LENGTH-len]);
                temp.writeBytes(data,len);
                return withValExpr(temp.readLong(),processContext);
            }
        }
    }

    @Override
    public void deProcess(Double data, ByteBuf dest, FieldDeProcessContext processContext) {
        Objects.requireNonNull(data);
        checkValRpnNull(processContext);
        long longData=data.longValue();
        int len=processContext.getLen();
        if(len==BYTE_LENGTH){
            dest.writeLong(longData);
        }else if(len>BYTE_LENGTH){
            dest.writeBytes(new byte[len-BYTE_LENGTH]);
            dest.writeLong(longData);
        }else{
            for(int i=len;i>=1;i--){
                int move=8*(i-1);
                dest.writeByte((byte)(longData>>>move));
            }
        }
    }
}
