package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.base.exception.BaseRuntimeException;
import com.bcd.nettyserver.tcp.parse.FieldParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.util.Objects;


public class IntegerArrayFieldParser implements FieldParser<int[]> {
    public final static int BYTE_LENGTH=4;
    @Override
    public int[] parse(ByteBuf data, int len,Object instance, Object... ext) {
        int singleLen=(int)ext[0];
        int[] res=new int[len/singleLen];
        //优化处理 short->int
        if(singleLen==2){
            for(int i=0;i<res.length;i++){
                res[i]=data.readUnsignedShort();
            }
        }else{
            if(singleLen==BYTE_LENGTH){
                for(int i=0;i<res.length;i++){
                    res[i]=data.readInt();
                }
            }else if(singleLen>BYTE_LENGTH){
                int diff=singleLen-BYTE_LENGTH;
                for(int i=0;i<res.length;i++){
                    data.skipBytes(diff);
                    res[i]=data.readInt();
                }
            }else{
                ByteBuf temp=Unpooled.buffer(len,len);
                int diff=BYTE_LENGTH-singleLen;
                for(int i=0;i<res.length;i++){
                    temp.writeBytes(new byte[diff]);
                    temp.writeBytes(data,BYTE_LENGTH);
                    res[i]=temp.readInt();
                    temp.clear();
                }
            }
        }
        return res;
    }

    @Override
    public String toHex(int[] data, int len, Object... ext) {
        checkHexData(data);
        int singleLen=(int)ext[0];
        if(data.length*singleLen!=len){
            throw BaseRuntimeException.getException("toHex error,data length["+data.length+"],len["+len+"],singleLen["+singleLen+"]");
        }
        ByteBuf byteBuf= Unpooled.buffer(len,len);
        if(singleLen==BYTE_LENGTH){
            for (int num : data) {
                byteBuf.writeInt(num);
            }
        }else if(singleLen>BYTE_LENGTH){
            for (int num : data) {
                byteBuf.writeBytes(new byte[singleLen-BYTE_LENGTH]);
                byteBuf.writeInt(num);
            }
        }else{
            for (int num : data) {
                for(int i=singleLen;i>=1;i--){
                    int move=8*(i-1);
                    byteBuf.writeByte((byte)(num>>>move));
                }
            }
        }
        return ByteBufUtil.hexDump(byteBuf);
    }
}
