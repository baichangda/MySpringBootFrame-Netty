package com.bcd.nettyserver.tcp.process.impl;

import com.bcd.base.exception.BaseRuntimeException;
import com.bcd.nettyserver.tcp.process.FieldDeProcessContext;
import com.bcd.nettyserver.tcp.process.FieldProcessContext;
import com.bcd.nettyserver.tcp.process.FieldProcessor;
import io.netty.buffer.ByteBuf;

import java.util.Objects;

public class ByteArrayProcessor extends FieldProcessor<byte[]> {
    private final static int BYTE_LENGTH=1;
    @Override
    public boolean support(Class clazz) {
        return clazz==byte[].class;
    }

    @Override
    public byte[] process(ByteBuf data, FieldProcessContext processContext)  {
        int singleLen= processContext.getFieldInfo().getPacketField_singleLen();
        byte[] res;
        if(singleLen==BYTE_LENGTH){
            res=new byte[processContext.getLen()];
            data.readBytes(res);
        }else if(singleLen>BYTE_LENGTH){
            res=new byte[processContext.getLen()/singleLen];
            int diff=singleLen-BYTE_LENGTH;
            for(int i=0;i<res.length;i++){
                data.skipBytes(diff);
                res[i]=data.readByte();
            }
        }else{
            throw BaseRuntimeException.getException("packetField_singleLen can not less than 1");
        }
        return res;
    }

    @Override
    public void deProcess(byte[] data, ByteBuf dest, FieldDeProcessContext processContext) {
        Objects.requireNonNull(data);
        int singleLen= processContext.getFieldInfo().getPacketField_singleLen();
        if(singleLen==BYTE_LENGTH){
            dest.writeBytes(data);
        }else if(singleLen>BYTE_LENGTH){
            for (byte num : data) {
                dest.writeBytes(new byte[singleLen-BYTE_LENGTH]);
                dest.writeByte(num);
            }
        }
    }

    public static void main(String[] args) {
        ByteArrayProcessor processor=new ByteArrayProcessor();
        byte[] b1=new byte[0];
        Byte[] b2=new Byte[0];
        System.out.println(processor.support(b1.getClass()));
        System.out.println(processor.support(b2.getClass()));
    }
}
