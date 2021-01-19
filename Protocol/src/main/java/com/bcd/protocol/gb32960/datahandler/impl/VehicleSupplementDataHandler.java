package com.bcd.protocol.gb32960.datahandler.impl;

import com.bcd.parser.impl.gb32960.data.Packet;
import com.bcd.parser.impl.gb32960.data.VehicleSupplementData;
import com.bcd.protocol.gb32960.datahandler.DataHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

@Component
public class VehicleSupplementDataHandler extends DataHandler<Packet> {
    public VehicleSupplementDataHandler() {
        super(0x03);
    }

    @Override
    public void handle(Packet data,ChannelHandlerContext ctx) {
        logger.info("receive data vin[{}]",data.getVin());
    }

}
