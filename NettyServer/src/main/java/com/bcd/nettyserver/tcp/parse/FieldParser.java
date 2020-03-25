package com.bcd.nettyserver.tcp.parse;


import com.bcd.base.exception.BaseRuntimeException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

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

    /**
     * 按照期望长度和实际长度取出数据
     * 如果expectLen<actualLen，则跳过多余的数据，取结尾数据
     * 如果expectLen>actualLen，则在前面补0
     * @param data 数据源
     * @param expectByteLen 数据期望的长度
     * @param actualByteLen 实际长度
     */
    default ByteBuf toByteBuf(ByteBuf data,int expectByteLen,int actualByteLen){
        ByteBuf temp= Unpooled.buffer(expectByteLen,expectByteLen);
        if(expectByteLen==actualByteLen){
            temp.writeBytes(data,expectByteLen);
        }else if(expectByteLen<actualByteLen){
            data.skipBytes(actualByteLen-expectByteLen);
            temp.writeBytes(data,expectByteLen);
        }else {
            for (int i = 1; i <= expectByteLen - actualByteLen; i++) {
                temp.writeByte(0);
            }
            temp.writeBytes(data, actualByteLen);
        }
        return temp;
    }
}
