package com.bcd.protocol.gb32960.data;


import com.bcd.nettyserver.tcp.anno.PacketField;
import com.bcd.protocol.gb32960.parse.impl.VehicleCommonDataFieldHandler;

import java.util.Date;

public class VehicleSupplementData extends PacketData {
    //数据采集时间
    @PacketField(index = 1,len = 6)
    Date collectTime;

    //车辆运行通用数据
    @PacketField(index = 2,handleClass = VehicleCommonDataFieldHandler.class)
    VehicleCommonData vehicleCommonData;

    public Date getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Date collectTime) {
        this.collectTime = collectTime;
    }

    public VehicleCommonData getVehicleCommonData() {
        return vehicleCommonData;
    }

    public void setVehicleCommonData(VehicleCommonData vehicleCommonData) {
        this.vehicleCommonData = vehicleCommonData;
    }
}
