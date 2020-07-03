package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.base.exception.BaseRuntimeException;
import com.bcd.nettyserver.tcp.parse.FieldParseContext;
import com.bcd.nettyserver.tcp.parse.FieldParser;
import com.bcd.nettyserver.tcp.parse.FieldToByteBufContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;


public class ShortArrayFieldParser implements FieldParser<short[]> {
    public final static int BYTE_LENGTH=2;

    @Override
    public short[] parse(ByteBuf data, int len, FieldParseContext context) {
        int singleLen=context.getFieldInfo().getPacketField_singleLen();
        short[] res;
        //优化处理 byte->short
        if(singleLen==1){
            res=new short[len];
            for(int i=0;i<res.length;i++){
                res[i]=data.readUnsignedByte();
            }
        }else{
            res=new short[len/singleLen];
            if(singleLen==BYTE_LENGTH){
                for(int i=0;i<res.length;i++){
                    res[i]=data.readShort();
                }
            }else if(singleLen>BYTE_LENGTH){
                int diff=singleLen-BYTE_LENGTH;
                for(int i=0;i<res.length;i++){
                    data.skipBytes(diff);
                    res[i]=data.readShort();
                }
            }else{
                ByteBuf temp=Unpooled.buffer(len,len);
                int diff=BYTE_LENGTH-singleLen;
                for(int i=0;i<res.length;i++){
                    temp.writeBytes(new byte[diff]);
                    temp.writeBytes(data,BYTE_LENGTH);
                    res[i]=temp.readShort();
                    temp.clear();
                }
            }
        }

        return res;
    }

    @Override
    public void toByteBuf(short[] data, int len, FieldToByteBufContext context,ByteBuf result) {
        checkByteBufData(data);
        int singleLen=context.getFieldInfo().getPacketField_singleLen();
        if(singleLen==BYTE_LENGTH){
            for (short num : data) {
                result.writeShort(num);
            }
        }else if(singleLen>BYTE_LENGTH){
            for (short num : data) {
                result.writeBytes(new byte[singleLen-BYTE_LENGTH]);
                result.writeShort(num);
            }
        }else{
            for (short num : data) {
                for(int i=singleLen;i>=1;i--){
                    int move=8*(i-1);
                    result.writeByte((byte)(num>>>move));
                }
            }
        }
    }
}
