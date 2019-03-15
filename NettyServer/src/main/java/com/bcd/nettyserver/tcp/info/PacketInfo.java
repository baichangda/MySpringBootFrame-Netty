package com.bcd.nettyserver.tcp.info;

import com.bcd.nettyserver.tcp.anno.PacketField;

import java.lang.reflect.Field;
import java.util.List;

public class PacketInfo {
    private byte[] header;
    private Integer lengthFieldStart;
    private Integer lengthFieldEnd;
    //标注@PacketField的字段集合和注解集合
    private List<Field> fieldList1;
    private List<PacketField> annoList1;

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

    public Integer getLengthFieldEnd() {
        return lengthFieldEnd;
    }

    public void setLengthFieldEnd(Integer lengthFieldEnd) {
        this.lengthFieldEnd = lengthFieldEnd;
    }

    public List<Field> getFieldList1() {
        return fieldList1;
    }

    public void setFieldList1(List<Field> fieldList1) {
        this.fieldList1 = fieldList1;
    }

    public List<PacketField> getAnnoList1() {
        return annoList1;
    }

    public void setAnnoList1(List<PacketField> annoList1) {
        this.annoList1 = annoList1;
    }

}
