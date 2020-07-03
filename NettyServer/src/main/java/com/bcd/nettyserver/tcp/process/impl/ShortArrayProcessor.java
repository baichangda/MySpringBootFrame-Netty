package com.bcd.nettyserver.tcp.process.impl;

import com.bcd.nettyserver.tcp.info.FieldInfo;
import com.bcd.nettyserver.tcp.process.FieldProcessContext;
import com.bcd.nettyserver.tcp.process.FieldProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Objects;

public class ShortArrayProcessor extends FieldProcessor<short[]> {
    private final static int BYTE_LENGTH=2;
    @Override
    public boolean support(Class clazz) {
        return false;
    }

    @Override
    public short[] process(ByteBuf data, Object instance, FieldProcessContext processContext) {
        int singleLen= processContext.getFieldInfo().getPacketField_singleLen();
        int len=processContext.getLen();
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
                ByteBuf temp= Unpooled.buffer(len,len);
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
    public void deProcess(short[] data, ByteBuf dest,FieldProcessContext processContext) {
        Objects.requireNonNull(data);
        int singleLen= processContext.getFieldInfo().getPacketField_singleLen();
        if(singleLen==BYTE_LENGTH){
            for (short num : data) {
                dest.writeShort(num);
            }
        }else if(singleLen>BYTE_LENGTH){
            for (short num : data) {
                dest.writeBytes(new byte[singleLen-BYTE_LENGTH]);
                dest.writeShort(num);
            }
        }else{
            for (short num : data) {
                for(int i=singleLen;i>=1;i--){
                    int move=8*(i-1);
                    dest.writeByte((byte)(num>>>move));
                }
            }
        }
    }
}
