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
     * @param context 解析上下文环境
     * @return
     */
    T parse(ByteBuf data, int len, FieldParseContext context);

    /**
     * 把对象转换为ByteBuf对象
     * @param data 数据集
     * @param len 数据长度 (len=0时表示无效,是否无效取决于字段注解 {@link PacketField#len()} ,{@link PacketField#lenExpr()} 是否有值)
     * @param context toByteBuf上下文环境
     * @param result 结果集
     * @return
     */
    default void toByteBuf(T data, int len, FieldToByteBufContext context,ByteBuf result){
        throw BaseRuntimeException.getException("toByteBuf not support");
    }

    default void checkByteBufData(T data){
        if(Objects.isNull(data)){
            throw BaseRuntimeException.getException("toByteBuf data can't be null");
        }
    }
}
