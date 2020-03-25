package com.bcd.protocol.gb32960.data;

import com.bcd.nettyserver.tcp.anno.PacketField;

/**
 * 每个可充电储能子系统电压数据格式
 */
public class StorageVoltageData {
    //可充电储能子系统号
    @PacketField(index = 1,len = 1)
    short no;

    //可充电储能装置电压
    @PacketField(index = 2,len = 2)
    int voltage;

    //可充电储能状态电流
    @PacketField(index = 3,len = 2)
    int current;

    //单体电池总数
    @PacketField(index = 4,len = 2)
    int total;

    //本帧起始电池序号
    @PacketField(index = 5,len = 2)
    int frameNo;

    //本帧单体电池总数
    @PacketField(index = 6,len = 1,var = "m")
    short frameTotal;

    //单体电池电压
    @PacketField(index = 7,singleLen = 2,lenExpr = "2*m")
    int[] singleVoltage;

    public short getNo() {
        return no;
    }

    public void setNo(short no) {
        this.no = no;
    }

    public int getVoltage() {
        return voltage;
    }

    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getFrameNo() {
        return frameNo;
    }

    public void setFrameNo(int frameNo) {
        this.frameNo = frameNo;
    }

    public short getFrameTotal() {
        return frameTotal;
    }

    public void setFrameTotal(short frameTotal) {
        this.frameTotal = frameTotal;
    }

    public int[] getSingleVoltage() {
        return singleVoltage;
    }

    public void setSingleVoltage(int[] singleVoltage) {
        this.singleVoltage = singleVoltage;
    }
}
