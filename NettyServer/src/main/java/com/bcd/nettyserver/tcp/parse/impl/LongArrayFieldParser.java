package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.nettyserver.tcp.parse.FieldParser;


public class LongArrayFieldParser implements FieldParser<long[]> {
    public final static LongArrayFieldParser INSTANCE=new LongArrayFieldParser();
    @Override
    public long[] parse(byte[] data,Object ...ext) {
        int singleLen=(int)ext[0];
        long[] res=new long[data.length/singleLen];
        for(int i=0;i<=res.length-1;i++){
            byte[] param=new byte[singleLen];
            for(int j=0;j<=singleLen-1;j++){
                param[j]=data[i*singleLen+j];
            }
            res[i]= LongFieldParser.INSTANCE.parse(param);
        }
        return res;
    }
}
