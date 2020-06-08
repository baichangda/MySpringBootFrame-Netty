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


public class LongArrayFieldParser implements FieldParser<long[]> {
    public final static int BYTE_LENGTH=8;
    @Override
    public long[] parse(ByteBuf data, int len, FieldParseContext context) {
        int singleLen=context.getFieldInfo().getPacketField_singleLen();
        long[] res=new long[len/singleLen];
        //优化处理 int->long
        if(singleLen==4){
            for(int i=0;i<res.length;i++){
                res[i]=data.readUnsignedInt();
            }
        }else{
            if(singleLen==BYTE_LENGTH){
                for(int i=0;i<res.length;i++){
                    res[i]=data.readLong();
                }
            }else if(singleLen>BYTE_LENGTH){
                int diff=singleLen-BYTE_LENGTH;
                for(int i=0;i<res.length;i++){
                    data.skipBytes(diff);
                    res[i]=data.readLong();
                }
            }else{
                ByteBuf temp=Unpooled.buffer(len,len);
                int diff=BYTE_LENGTH-singleLen;
                for(int i=0;i<res.length;i++){
                    temp.writeBytes(new byte[diff]);
                    temp.writeBytes(data,BYTE_LENGTH);
                    res[i]=temp.readLong();
                    temp.clear();
                }
            }
        }
        return res;
    }

    @Override
    public String toHex(long[] data, int len, FieldToHexContext context) {
        checkHexData(data);
        int singleLen=context.getFieldInfo().getPacketField_singleLen();
        if(data.length*singleLen!=len){
            throw BaseRuntimeException.getException("toHex error,data length["+data.length+"],len["+len+"],singleLen["+singleLen+"]");
        }
        ByteBuf byteBuf= Unpooled.buffer(len,len);
        if(singleLen==BYTE_LENGTH){
            for (long num : data) {
                byteBuf.writeLong(num);
            }
        }else if(singleLen>BYTE_LENGTH){
            for (long num : data) {
                byteBuf.writeBytes(new byte[singleLen-BYTE_LENGTH]);
                byteBuf.writeLong(num);
            }
        }else{
            for (long num : data) {
                for(int i=singleLen;i>=1;i--){
                    int move=8*(i-1);
                    byteBuf.writeByte((byte)(num>>>move));
                }
            }
        }
        return ByteBufUtil.hexDump(byteBuf);
    }
}
