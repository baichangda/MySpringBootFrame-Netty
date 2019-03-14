package com.bcd.protocol.gb32960.data;

import com.bcd.nettyserver.tcp.anno.PacketField;

import java.util.ArrayList;
import java.util.List;

/**
 * 可充电储能装置电压数据
 */
public class VehicleStorageVoltageData {
    //可充电储能子系统个数
    @PacketField(index = 1,len = 1,var = "len")
    short num;

    //可充电储能子系统电压信息集合
    @PacketField(index = 2,listLenExpr = "len")
    List<StorageVoltageData> content=new ArrayList<>();

    public short getNum() {
        return num;
    }

    public void setNum(short num) {
        this.num = num;
    }

    public List<StorageVoltageData> getContent() {
        return content;
    }

    public void setContent(List<StorageVoltageData> content) {
        this.content = content;
    }
}
