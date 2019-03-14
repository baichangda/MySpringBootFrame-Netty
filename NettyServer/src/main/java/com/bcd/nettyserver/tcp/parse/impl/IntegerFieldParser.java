package com.bcd.nettyserver.tcp.parse.impl;


public class IntegerFieldParser extends NumberFieldParser<Integer> {
    public final static IntegerFieldParser INSTANCE=new IntegerFieldParser();

    public IntegerFieldParser() {
        super(4);
    }

    @Override
    public Integer parse(byte[] data,Object ...ext) {
        return toByteBuf(data).readInt();
    }


}
