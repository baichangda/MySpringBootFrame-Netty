package com.bcd.nettyserver.tcp.info;


import java.util.List;

public class PacketInfo {
    //解析的字段信息集合
    private List<FieldInfo> fieldInfoList;

    public PacketInfo() {
    }

    public List<FieldInfo> getFieldInfoList() {
        return fieldInfoList;
    }

    public void setFieldInfoList(List<FieldInfo> fieldInfoList) {
        this.fieldInfoList = fieldInfoList;
    }
}
