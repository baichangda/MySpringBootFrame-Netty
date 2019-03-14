package com.bcd.nettyserver.tcp.parse.impl;

import com.bcd.nettyserver.tcp.parse.FieldParser;


public class ByteFieldParser implements FieldParser<Byte>{
    public final static ByteFieldParser INSTANCE=new ByteFieldParser();
    @Override
    public Byte parse(byte[] data,Object ...ext) {
        return data[0];
    }
}
