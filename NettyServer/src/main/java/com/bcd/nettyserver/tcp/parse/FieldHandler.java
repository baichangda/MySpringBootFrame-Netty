package com.bcd.nettyserver.tcp.parse;

import com.bcd.base.exception.BaseRuntimeException;
import io.netty.buffer.ByteBuf;

import java.util.Objects;

public interface FieldHandler<T> {
    T handle(ByteBuf data, Object... ext);

    void setParser(Parser parser);

    /**
     * 把对象转换为hex字符串
     * @param data 对象
     * @param ext 附加信息
     * @return
     */
    default String toHex(T data,Object ... ext){
        throw BaseRuntimeException.getException("toHex not support");
    }

    default void checkHexData(T data){
        if(Objects.isNull(data)){
            throw BaseRuntimeException.getException("toHex data can't be null");
        }
    }
}
