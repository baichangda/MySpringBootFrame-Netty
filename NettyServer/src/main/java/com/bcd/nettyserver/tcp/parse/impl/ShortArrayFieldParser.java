package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.nettyserver.tcp.parse.FieldParser;


public class ShortArrayFieldParser implements FieldParser<short[]> {
    public final static ShortArrayFieldParser INSTANCE=new ShortArrayFieldParser();
    @Override
    public short[] parse(byte[] data,Object ...ext) {
        int singleLen=(int)ext[0];
        short[] res=new short[data.length/singleLen];
        for(int i=0;i<=res.length-1;i++){
            byte[] param=new byte[singleLen];
            for(int j=0;j<=singleLen-1;j++){
                param[j]=data[i*singleLen+j];
            }
            res[i]=ShortFieldParser.INSTANCE.parse(param);
        }
        return res;
    }
}
