package com.bcd.nettyserver.tcp.parse;

import com.bcd.nettyserver.tcp.anno.PacketField;
import com.bcd.nettyserver.tcp.info.FieldInfo;
import com.bcd.nettyserver.tcp.info.PacketInfo;
import io.netty.buffer.ByteBuf;

public class FieldParseContext {
    /**
     * 所属对象 如果是顶层对象,则此为null
     */
    private Object instance;
    /**
     * 当前解析字段信息
     */
    private FieldInfo fieldInfo;
    /**
     * 当前字段所属类的信息
     */
    private PacketInfo packetInfo;

    /**
     * 当前字段所属对象总长度(0代表无效)
     * 判断是否为0,通过
     * #{@link PacketField#len()}
     * #{@link PacketField#lenExpr()}
     *  或者当其调用者 #{@link ParserContext#parse(Class, ByteBuf, int)} 时候 allLen参数不为0
     */
    private int allLen;

    public int getAllLen() {
        return allLen;
    }

    public void setAllLen(int allLen) {
        this.allLen = allLen;
    }

    public Object getInstance() {
        return instance;
    }

    public FieldInfo getFieldInfo() {
        return fieldInfo;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public void setFieldInfo(FieldInfo fieldInfo) {
        this.fieldInfo = fieldInfo;
    }

    public PacketInfo getPacketInfo() {
        return packetInfo;
    }

    public void setPacketInfo(PacketInfo packetInfo) {
        this.packetInfo = packetInfo;
    }



}
