package com.bcd.nettyserver.http.convert;

public interface NettyHttpParamConverter {
    <T> T convert(Object source, Class<T> targetType);
}
