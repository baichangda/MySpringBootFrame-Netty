package com.bcd.nettyserver.tcp.parse;


import com.bcd.base.exception.BaseRuntimeException;
import io.netty.buffer.ByteBuf;

import java.util.Objects;

public interface FieldParser<T> {
    /**
     * 从data中取出len字节解析为对应的结果
     * @param data 数据集
     * @param len 数据长度
     * @param ext 附加信息
     * @return
     */
    T parse(ByteBuf data,int len, Object... ext);

    /**
     * 把对象转换为hex字符串
     * @param data 对象
     * @param len 转换后字节长度
     * @param ext 附加信息
     * @return
     */
    default String toHex(T data,int len,Object ... ext){
        throw BaseRuntimeException.getException("toHex not support");
    }

    default void checkHexData(T data){
        if(Objects.isNull(data)){
            throw BaseRuntimeException.getException("toHex data can't be null");
        }
    }
}
