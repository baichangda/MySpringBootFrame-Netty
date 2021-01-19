package com.bcd.protocol.gb32960.datahandler.impl;

import com.bcd.parser.impl.gb32960.data.Packet;
import com.bcd.parser.impl.gb32960.data.PacketData;
import com.bcd.protocol.gb32960.datahandler.DataHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

@Component
public class VehicleHeartDataHandler extends DataHandler<Packet> {
    public VehicleHeartDataHandler() {
        super(0x07);
    }

    @Override
    public void handle(Packet data,ChannelHandlerContext ctx) {
        logger.info("receive data vin[{}]",data.getVin());
    }

}
