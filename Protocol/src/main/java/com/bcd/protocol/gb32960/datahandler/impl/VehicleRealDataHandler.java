package com.bcd.protocol.gb32960.datahandler.impl;

import com.bcd.protocol.gb32960.data.VehicleRealData;
import com.bcd.protocol.gb32960.datahandler.DataHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

@Component
public class VehicleRealDataHandler extends DataHandler<VehicleRealData> {
    public VehicleRealDataHandler() {
        super(0x02);
    }

    @Override
    public void handle(VehicleRealData data, ChannelHandlerContext ctx) {
    }

}
