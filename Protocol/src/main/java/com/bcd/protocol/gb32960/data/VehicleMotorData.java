package com.bcd.protocol.gb32960.data;

import com.bcd.nettyserver.tcp.anno.PacketField;

import java.util.ArrayList;
import java.util.List;

/**
 * 驱动电机数据
 */
public class VehicleMotorData {
    //驱动电机个数
    @PacketField(index = 1,len = 1,var = "len")
    short num;

    //驱动电机总成信息列表
    @PacketField(index = 2,listLenExpr = "len")
    List<MotorData> content=new ArrayList<>();

    public short getNum() {
        return num;
    }

    public void setNum(short num) {
        this.num = num;
    }

    public List<MotorData> getContent() {
        return content;
    }

    public void setContent(List<MotorData> content) {
        this.content = content;
    }
}
