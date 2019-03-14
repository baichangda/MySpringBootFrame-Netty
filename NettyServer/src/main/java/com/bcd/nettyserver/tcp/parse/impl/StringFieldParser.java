package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.nettyserver.tcp.parse.FieldParser;

public class StringFieldParser implements FieldParser<String> {
    public final static StringFieldParser INSTANCE=new StringFieldParser();
    @Override
    public String parse(byte[] data,Object ...ext) {
        int discardLen=0;
        for(int i=data.length-1;i>=0;i--){
            if(data[i]==0){
                discardLen++;
            }else{
                break;
            }
        }
        byte[] newData=new byte[data.length-discardLen];
        System.arraycopy(data,0,newData,0,newData.length);
        return new String(newData);
    }
}
