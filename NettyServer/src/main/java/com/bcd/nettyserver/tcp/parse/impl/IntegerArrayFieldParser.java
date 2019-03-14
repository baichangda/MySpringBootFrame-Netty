package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.nettyserver.tcp.parse.FieldParser;


public class IntegerArrayFieldParser implements FieldParser<int[]> {
    public final static IntegerArrayFieldParser INSTANCE=new IntegerArrayFieldParser();
    @Override
    public int[] parse(byte[] data,Object ...ext) {
        int singleLen=(int)ext[0];
        int[] res=new int[data.length/singleLen];
        for(int i=0;i<=res.length-1;i++){
            byte[] param=new byte[singleLen];
            for(int j=0;j<=singleLen-1;j++){
                param[j]=data[i*singleLen+j];
            }
            res[i]= IntegerFieldParser.INSTANCE.parse(param);
        }
        return res;
    }
}
