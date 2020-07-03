package com.bcd.nettyserver.tcp.process.impl;

import com.bcd.nettyserver.tcp.info.FieldInfo;
import com.bcd.nettyserver.tcp.process.FieldProcessContext;
import com.bcd.nettyserver.tcp.process.FieldProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Objects;

public class ByteBufProcessor extends FieldProcessor<ByteBuf> {
    @Override
    public boolean support(Class clazz) {
        return clazz==ByteBuf.class;
    }

    @Override
    public ByteBuf process(ByteBuf data, Object instance, FieldProcessContext processContext) {
        int len=processContext.getLen();
        ByteBuf byteBuf= Unpooled.buffer(len,len);
        byteBuf.writeBytes(data,len);
        return byteBuf;
    }

    @Override
    public void deProcess(ByteBuf data, ByteBuf dest, FieldProcessContext processContext) {
        Objects.requireNonNull(data);
        dest.writeBytes(data);
    }
}
