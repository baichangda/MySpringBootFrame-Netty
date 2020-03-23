package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.nettyserver.tcp.parse.FieldParser;


public class ByteArrayFieldParser implements FieldParser<byte[]> {
    public final static ByteArrayFieldParser INSTANCE=new ByteArrayFieldParser();
    @Override
    public byte[] parse(byte[] data,Object ...ext) {
        int singleLen=(int)ext[0];
        byte[] res=new byte[data.length/singleLen];
        for(int i=0;i<=res.length-1;i++){
            byte[] param=new byte[singleLen];
            for(int j=0;j<=singleLen-1;j++){
                param[j]=data[i*singleLen+j];
            }
            res[i]= param[0];
        }
        return res;
    }
}
