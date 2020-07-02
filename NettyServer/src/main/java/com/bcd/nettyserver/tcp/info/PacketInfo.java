package com.bcd.nettyserver.tcp.info;


import com.bcd.nettyserver.tcp.anno.PacketField;

import java.util.List;

public class PacketInfo {
    //解析的字段信息集合
    private List<FieldInfo> fieldInfoList;
    //计算偏移量字段集合
    private List<OffsetFieldInfo> offsetFieldInfoList;
    /**
     * 变量个数,包括变量个数
     * {@link PacketField#var()}
     */
    public int varCount;

    public PacketInfo() {
    }

    public List<FieldInfo> getFieldInfoList() {
        return fieldInfoList;
    }

    public void setFieldInfoList(List<FieldInfo> fieldInfoList) {
        this.fieldInfoList = fieldInfoList;
    }

    public List<OffsetFieldInfo> getOffsetFieldInfoList() {
        return offsetFieldInfoList;
    }

    public void setOffsetFieldInfoList(List<OffsetFieldInfo> offsetFieldInfoList) {
        this.offsetFieldInfoList = offsetFieldInfoList;
    }

    public int getVarCount() {
        return varCount;
    }

    public void setVarCount(int varCount) {
        this.varCount = varCount;
    }
}
