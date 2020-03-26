package com.bcd.nettyserver.tcp.parse;


import com.bcd.base.exception.BaseRuntimeException;
import com.bcd.nettyserver.tcp.anno.PacketField;
import io.netty.buffer.ByteBuf;

import java.util.Objects;

public interface FieldParser<T> {

    default void setContext(ParserContext parser){
        
    }

    /**
     * 从data中取出len字节解析为对应的结果
     * @param data 数据集
     * @param len 数据长度 (len=0时表示无效,是否无效取决于字段注解 {@link PacketField#len()} ,{@link PacketField#lenExpr()} 是否有值)
     * @param instance 所属对象 如果是顶层对象,则此为null
     * @param ext 附加信息
     * @return
     */
    T parse(ByteBuf data,int len,Object instance, Object... ext);

    /**
     * 把对象转换为hex字符串
     * @param data 对象
     * @param len 转换后字节长度 (len=0时表示无效,是否无效取决于字段注解 {@link PacketField#len()} ,{@link PacketField#lenExpr()} 是否有值)
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
