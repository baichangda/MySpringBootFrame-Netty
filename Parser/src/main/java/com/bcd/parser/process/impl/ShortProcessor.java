package com.bcd.parser.process.impl;

import com.bcd.base.util.RpnUtil;
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
        short res;
        int len=processContext.getLen();
        if(len==1){
            //优化处理 byte->short
            res=data.readUnsignedByte();
        }else {
            if (len == BYTE_LENGTH) {
                res=data.readShort();
            } else if (len > BYTE_LENGTH) {
                data.skipBytes(len - BYTE_LENGTH);
                res=data.readShort();
            } else {
                ByteBuf temp= Unpooled.buffer(BYTE_LENGTH,BYTE_LENGTH);
                temp.writeBytes(new byte[BYTE_LENGTH-len]);
                temp.writeBytes(data,len);
                res=temp.readShort();
            }
        }
        Object[] valRpn=processContext.getFieldInfo().getValRpn();
        if(valRpn==null){
            return res;
        }else{
            return (short)RpnUtil.calcRPN_char_double_singleVar(valRpn,res);
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
