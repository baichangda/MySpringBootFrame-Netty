package com.bcd.nettyserver.tcp.parse;


public interface FieldParser<T> {
    T parse(byte[] data, Object... ext);
}
