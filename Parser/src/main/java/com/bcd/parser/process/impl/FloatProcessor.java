package com.bcd.parser.process.impl;

import com.bcd.parser.process.FieldDeProcessContext;
import com.bcd.parser.process.FieldProcessContext;
import com.bcd.parser.process.FieldProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Objects;

public class FloatProcessor extends FieldProcessor<Float> {
    public final static int BYTE_LENGTH=4;

    @Override
    public Float process(ByteBuf data, FieldProcessContext processContext) {
        int len=processContext.getLen();
        if(len==2){
            //优化处理 short->int
            return (float)withValExpr(data.readUnsignedShort(),processContext);
        }else {
            if (len == BYTE_LENGTH) {
                return (float)withValExpr(data.readInt(),processContext);
            } else if (len > BYTE_LENGTH) {
                data.skipBytes(len - BYTE_LENGTH);
                return (float)withValExpr(data.readInt(),processContext);
            } else {
                ByteBuf temp= Unpooled.buffer(BYTE_LENGTH,BYTE_LENGTH);
                temp.writeBytes(new byte[BYTE_LENGTH-len]);
                temp.writeBytes(data,len);
                return (float)withValExpr(temp.readInt(),processContext);
            }
        }
    }

    @Override
    public void deProcess(Float data, ByteBuf dest, FieldDeProcessContext processContext) {
        Objects.requireNonNull(data);
        checkValRpnNull(processContext);
        int intData=data.intValue();
        int len=processContext.getLen();
        if(len==BYTE_LENGTH){
            dest.writeInt(intData);
        }else if(len>BYTE_LENGTH){
            dest.writeBytes(new byte[len-BYTE_LENGTH]);
            dest.writeInt(intData);
        }else{
            for(int i=len;i>=1;i--){
                int move=8*(i-1);
                dest.writeByte((byte)(intData>>>move));
            }
        }
    }
}
