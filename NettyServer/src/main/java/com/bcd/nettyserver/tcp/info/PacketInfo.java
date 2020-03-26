package com.bcd.nettyserver.tcp.info;


import java.util.List;

public class PacketInfo {
    //解析的字段信息集合
    private List<FieldInfo> fieldInfoList;
    //计算偏移量字段集合
    private List<OffsetFieldInfo> offsetFieldInfoList;

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
}
