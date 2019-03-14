package com.bcd.protocol.gb32960.datahandler.impl;

import com.bcd.protocol.gb32960.data.PacketData;
import com.bcd.protocol.gb32960.datahandler.DataHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

@Component
public class VehicleValidateTimeHandler extends DataHandler<PacketData> {
    public VehicleValidateTimeHandler() {
        super(0x08);
    }

    @Override
    public void handle(PacketData data,ChannelHandlerContext ctx) {
    }

}
