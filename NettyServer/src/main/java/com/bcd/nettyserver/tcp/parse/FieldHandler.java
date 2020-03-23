package com.bcd.nettyserver.tcp.parse;

import io.netty.buffer.ByteBuf;

public interface FieldHandler<T> {
    T handle(ByteBuf data, Object... ext);

    void setParser(Parser parser);
}
