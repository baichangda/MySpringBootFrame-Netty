package com.bcd.nettyserver.tcp.parse;

import com.bcd.nettyserver.tcp.info.FieldInfo;

public class FieldToHexContext {
    /**
     * 当前解析字段信息
     */
    private FieldInfo fieldInfo;

    public FieldInfo getFieldInfo() {
        return fieldInfo;
    }

    public void setFieldInfo(FieldInfo fieldInfo) {
        this.fieldInfo = fieldInfo;
    }
}
