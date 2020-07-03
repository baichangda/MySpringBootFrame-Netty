package com.bcd.nettyserver.tcp.process;

import com.bcd.nettyserver.tcp.anno.PacketField;
import com.bcd.nettyserver.tcp.info.FieldInfo;
import io.netty.buffer.ByteBuf;

public class FieldDeProcessContext {
    /**
     * 字段信息
     */
    FieldInfo fieldInfo;

    /**
     * 字段值占用字节长度
     * 取自
     * {@link PacketField#len()}、{@link PacketField#lenExpr()}
     */
    int len;

    /**
     * 集合长度,只有List类型时候才有效
     * 取自
     * {@link PacketField#listLenExpr()}}
     */
    int listLen;

    /**
     * 字段所属实例
     */
    Object instance;


    public FieldInfo getFieldInfo() {
        return fieldInfo;
    }

    public void setFieldInfo(FieldInfo fieldInfo) {
        this.fieldInfo = fieldInfo;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public int getListLen() {
        return listLen;
    }

    public void setListLen(int listLen) {
        this.listLen = listLen;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }
}
