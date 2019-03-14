package com.bcd.protocol.gb32960.datahandler.impl;

import com.bcd.protocol.gb32960.data.VehicleLoginData;
import com.bcd.protocol.gb32960.datahandler.DataHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unchecked")
public class VehicleLoginDataHandler extends DataHandler<VehicleLoginData> {
    public VehicleLoginDataHandler() {
        super(0x01);
    }

    @Override
    public void handle(VehicleLoginData data,ChannelHandlerContext ctx) {
    }

}
