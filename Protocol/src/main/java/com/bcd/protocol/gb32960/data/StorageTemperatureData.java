package com.bcd.protocol.gb32960.data;

import com.bcd.nettyserver.tcp.anno.PacketField;

/**
 * 每个可充电储能子系统上温度数据格式
 */
public class StorageTemperatureData {
    //可充电储能子系统号
    @PacketField(index = 1,len = 1)
    short no;

    //可充电储能温度探针个数
    @PacketField(index = 2,len = 2,var = "n")
    int num;

    //可充电储能子系统各温度探针检测到的温度值
    @PacketField(index = 3,lenExpr = "n")
    short[] temperatures;

    public short getNo() {
        return no;
    }

    public void setNo(short no) {
        this.no = no;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public short[] getTemperatures() {
        return temperatures;
    }

    public void setTemperatures(short[] temperatures) {
        this.temperatures = temperatures;
    }
}
