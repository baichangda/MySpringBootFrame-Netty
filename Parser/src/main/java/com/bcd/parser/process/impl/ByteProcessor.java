package com.bcd.parser.process.impl;


import com.bcd.base.exception.BaseRuntimeException;
import com.bcd.parser.process.FieldDeProcessContext;
import com.bcd.parser.process.FieldProcessContext;
import com.bcd.parser.process.FieldProcessor;
import io.netty.buffer.ByteBuf;

import java.util.Objects;

public class ByteProcessor extends FieldProcessor<Byte> {
    private final static int BYTE_LENGTH=1;

    @Override
    public Byte process(ByteBuf data, FieldProcessContext processContext) {
        int len=processContext.getLen();
        if(len==BYTE_LENGTH){
            return (byte)withValExpr(data.readByte(),processContext);
        }else if(len>BYTE_LENGTH){
            data.skipBytes(len-BYTE_LENGTH);
            return (byte)withValExpr(data.readByte(),processContext);
        }else{
            return (byte)withValExpr(0,processContext);
        }
    }

    @Override
    public void deProcess(Byte data, ByteBuf dest, FieldDeProcessContext processContext) {
        Objects.requireNonNull(data);
        checkValRpnNull(processContext);
        if(processContext.getFieldInfo().getValRpn()!=null){
            throw BaseRuntimeException.getException("class[{0}] field[{1}] not support",processContext.getFieldInfo().getClazz().getName(),processContext.getFieldInfo().getField().getName());
        }
        int len=processContext.getLen();
        byte[] content=new byte[len];
        content[len-BYTE_LENGTH]=data;
        dest.writeBytes(content);
    }
}
