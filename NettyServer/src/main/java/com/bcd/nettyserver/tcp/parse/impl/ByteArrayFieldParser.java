package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.base.exception.BaseRuntimeException;
import com.bcd.nettyserver.tcp.parse.FieldParseContext;
import com.bcd.nettyserver.tcp.parse.FieldParser;
import com.bcd.nettyserver.tcp.parse.FieldToByteBufContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;


public class ByteArrayFieldParser implements FieldParser<byte[]> {
    public final static int BYTE_LENGTH=1;
    @Override
    public byte[] parse(ByteBuf data, int len, FieldParseContext context) {
        int singleLen=context.getFieldInfo().getPacketField_singleLen();
        byte[] res;
        if(singleLen==BYTE_LENGTH){
            res=new byte[len];
            data.readBytes(res);
        }else if(singleLen>BYTE_LENGTH){
            res=new byte[len/singleLen];
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
    public ByteBuf toByteBuf(byte[] data, int len, FieldToByteBufContext context) {
        checkByteBufData(data);
        int singleLen=context.getFieldInfo().getPacketField_singleLen();
        ByteBuf byteBuf= Unpooled.buffer(len,len);
        if(singleLen==BYTE_LENGTH){
            byteBuf.writeBytes(data);
        }else if(singleLen>BYTE_LENGTH){
            for (byte num : data) {
                byteBuf.writeBytes(new byte[singleLen-BYTE_LENGTH]);
                byteBuf.writeByte(num);
            }
        }
        return byteBuf;
    }
}
