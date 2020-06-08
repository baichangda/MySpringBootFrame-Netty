package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.base.exception.BaseRuntimeException;
import com.bcd.nettyserver.tcp.info.FieldInfo;
import com.bcd.nettyserver.tcp.parse.FieldParseContext;
import com.bcd.nettyserver.tcp.parse.FieldParser;
import com.bcd.nettyserver.tcp.parse.FieldToHexContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.util.Objects;


public class ByteArrayFieldParser implements FieldParser<byte[]> {
    public final static int BYTE_LENGTH=1;
    @Override
    public byte[] parse(ByteBuf data, int len, FieldParseContext context) {
        int singleLen=context.getFieldInfo().getPacketField_singleLen();
        byte[] res=new byte[len/singleLen];
        if(singleLen==BYTE_LENGTH){
            data.readBytes(res);
        }else if(singleLen>BYTE_LENGTH){
            int diff=singleLen-BYTE_LENGTH;
            for(int i=0;i<res.length;i++){
                data.skipBytes(diff);
                res[i]=data.readByte();
            }
        }
        return res;
    }

    @Override
    public String toHex(byte[] data, int len, FieldToHexContext context) {
        checkHexData(data);
        int singleLen=context.getFieldInfo().getPacketField_singleLen();
        if(data.length*singleLen!=len){
            throw BaseRuntimeException.getException("toHex error,data length["+data.length+"],len["+len+"],singleLen["+singleLen+"]");
        }
        ByteBuf byteBuf= Unpooled.buffer(len,len);
        if(singleLen==BYTE_LENGTH){
            byteBuf.writeBytes(data);
        }else if(singleLen>BYTE_LENGTH){
            for (byte num : data) {
                byteBuf.writeBytes(new byte[singleLen-BYTE_LENGTH]);
                byteBuf.writeByte(num);
            }
        }
        return ByteBufUtil.hexDump(byteBuf);
    }
}
