package com.bcd.protocol.gb32960.data;

import com.bcd.nettyserver.tcp.anno.PacketField;
import com.bcd.nettyserver.tcp.anno.Processable;

/**
 * 发动机数据
 */
@Processable
public class VehicleEngineData {
    //发动机状态
    @PacketField(index = 1,len = 1)
    short status;

    //曲轴转速
    @PacketField(index = 2,len = 2)
    int speed;

    //燃料消耗率
    @PacketField(index = 3,len = 2)
    int rate;

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
