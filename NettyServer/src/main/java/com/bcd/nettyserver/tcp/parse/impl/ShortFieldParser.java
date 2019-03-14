package com.bcd.nettyserver.tcp.parse.impl;

public class ShortFieldParser extends NumberFieldParser<Short> {
    public final static ShortFieldParser INSTANCE=new ShortFieldParser();
    public ShortFieldParser() {
        super(2);
    }

    @Override
    public Short parse(byte[] data,Object ...ext) {
        return toByteBuf(data).readShort();
    }
}
