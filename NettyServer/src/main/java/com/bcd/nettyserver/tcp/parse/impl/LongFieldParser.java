package com.bcd.nettyserver.tcp.parse.impl;


public class LongFieldParser extends NumberFieldParser<Long> {
    public final static LongFieldParser INSTANCE=new LongFieldParser();

    public LongFieldParser() {
        super(8);
    }

    @Override
    public Long parse(byte[] data,Object ...ext) {
        return toByteBuf(data).readLong();
    }

}
