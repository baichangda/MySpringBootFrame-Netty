package com.bcd.parser.info;



import java.util.ArrayList;

public class PacketInfo {
    //解析的字段信息集合
    private FieldInfo[] fieldInfos;
    //计算偏移量字段集合
    private OffsetFieldInfo[] offsetFieldInfos;

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

    public FieldInfo[] getFieldInfos() {
        return fieldInfos;
    }

    public void setFieldInfos(FieldInfo[] fieldInfos) {
        this.fieldInfos = fieldInfos;
    }

    public OffsetFieldInfo[] getOffsetFieldInfos() {
        return offsetFieldInfos;
    }

    public void setOffsetFieldInfos(OffsetFieldInfo[] offsetFieldInfos) {
        this.offsetFieldInfos = offsetFieldInfos;
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
