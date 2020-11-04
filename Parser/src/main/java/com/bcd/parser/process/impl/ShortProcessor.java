package com.bcd.parser.process.impl;

import com.bcd.parser.process.FieldDeProcessContext;
import com.bcd.parser.process.FieldProcessContext;
import com.bcd.parser.process.FieldProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Objects;

public class ShortProcessor extends FieldProcessor<Short> {
    private final static int BYTE_LENGTH=2;

    @Override
    public Short process(ByteBuf data, FieldProcessContext processContext) {
        int len=processContext.getLen();
        if(len==1){
            //优化处理 byte->short
            return (short)withValExpr(data.readUnsignedByte(),processContext);
        }else {
            if (len == BYTE_LENGTH) {
                return (short)withValExpr(data.readShort(),processContext);
            } else if (len > BYTE_LENGTH) {
                data.skipBytes(len - BYTE_LENGTH);
                return (short)withValExpr(data.readShort(),processContext);
            } else {
                ByteBuf temp= Unpooled.buffer(BYTE_LENGTH,BYTE_LENGTH);
                temp.writeBytes(new byte[BYTE_LENGTH-len]);
                temp.writeBytes(data,len);
                return (short)withValExpr(temp.readShort(),processContext);
            }
        }
    }

    @Override
    public void deProcess(Short data, ByteBuf dest, FieldDeProcessContext processContext) {
        Objects.requireNonNull(data);
        checkValRpnNull(processContext);
        int len=processContext.getLen();
        if(len==BYTE_LENGTH){
            dest.writeShort(data);
        }else if(len>BYTE_LENGTH){
            dest.writeBytes(new byte[len-BYTE_LENGTH]);
            dest.writeShort(data);
        }else{
            for(int i=len;i>=1;i--){
                int move=8*(i-1);
                dest.writeByte((byte)(data>>>move));
            }
        }
    }
}
