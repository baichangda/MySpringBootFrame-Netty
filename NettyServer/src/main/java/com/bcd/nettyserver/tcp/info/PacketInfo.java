package com.bcd.nettyserver.tcp.info;


import java.util.List;

public class PacketInfo {
    private byte[] header;
    private Integer lengthFieldStart;
    private Integer lengthFieldLength;
    //解析的字段信息集合
    private List<FieldInfo> fieldInfoList;

    public PacketInfo() {
    }

    public byte[] getHeader() {
        return header;
    }

    public void setHeader(byte[] header) {
        this.header = header;
    }

    public Integer getLengthFieldStart() {
        return lengthFieldStart;
    }

    public void setLengthFieldStart(Integer lengthFieldStart) {
        this.lengthFieldStart = lengthFieldStart;
    }

    public Integer getLengthFieldLength() {
        return lengthFieldLength;
    }

    public void setLengthFieldLength(Integer lengthFieldLength) {
        this.lengthFieldLength = lengthFieldLength;
    }

    public List<FieldInfo> getFieldInfoList() {
        return fieldInfoList;
    }

    public void setFieldInfoList(List<FieldInfo> fieldInfoList) {
        this.fieldInfoList = fieldInfoList;
    }
}
