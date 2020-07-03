package com.bcd.nettyserver.tcp.info;


import java.util.List;

public class PacketInfo {
    //解析的字段信息集合
    private List<FieldInfo> fieldInfoList;
    //计算偏移量字段集合
    private List<OffsetFieldInfo> offsetFieldInfoList;

    /**
     * 变量值数组长度,相对于char而言
     */
    public int varValArrLen=0;

    /**
     * 变量值数据偏移量,相对于char而言
     */
    public int varValArrOffset=0;

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

    public int getVarValArrLen() {
        return varValArrLen;
    }

    public void setVarValArrLen(int varValArrLen) {
        this.varValArrLen = varValArrLen;
    }

    public int getVarValArrOffset() {
        return varValArrOffset;
    }

    public void setVarValArrOffset(int varValArrOffset) {
        this.varValArrOffset = varValArrOffset;
    }
}
