package com.bcd.protocol.gb32960.data;

import com.bcd.nettyserver.tcp.anno.PacketField;
import com.bcd.nettyserver.tcp.anno.ParseAble;

import java.util.Date;

@ParseAble
public class VehicleLogoutData extends PacketData{
    //登出时间
    @PacketField(index = 1,len = 6)
    Date collectTime;

    //登出流水号
    @PacketField(index = 2,len = 2)
    int sn;

    @Override
    public Date getCollectTime() {
        return collectTime;
    }

    @Override
    public void setCollectTime(Date collectTime) {
        this.collectTime = collectTime;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }
}
