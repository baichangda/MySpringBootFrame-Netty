package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.base.exception.BaseRuntimeException;
import com.bcd.nettyserver.tcp.parse.FieldParseContext;
import com.bcd.nettyserver.tcp.parse.FieldParser;
import com.bcd.nettyserver.tcp.parse.FieldToByteBufContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;


public class IntegerArrayFieldParser implements FieldParser<int[]> {
    public final static int BYTE_LENGTH=4;
    @Override
    public int[] parse(ByteBuf data, int len, FieldParseContext context) {
        int singleLen=context.getFieldInfo().getPacketField_singleLen();
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
    public ByteBuf toByteBuf(int[] data, int len, FieldToByteBufContext context) {
        checkByteBufData(data);
        int singleLen=context.getFieldInfo().getPacketField_singleLen();
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
        return byteBuf;
    }
}
