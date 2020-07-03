package com.bcd.nettyserver.tcp.process;

import com.bcd.nettyserver.tcp.anno.PacketField;
import com.bcd.nettyserver.tcp.info.FieldInfo;
import io.netty.buffer.ByteBuf;

public class FieldProcessContext {
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
     * 对象占用字节长度
     * 0时候不可用,依赖于{@link Processor#process(Class, ByteBuf, int)} 中第三个参数
     */
    int instanceLen;

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

    public int getInstanceLen() {
        return instanceLen;
    }

    public void setInstanceLen(int instanceLen) {
        this.instanceLen = instanceLen;
    }
}
