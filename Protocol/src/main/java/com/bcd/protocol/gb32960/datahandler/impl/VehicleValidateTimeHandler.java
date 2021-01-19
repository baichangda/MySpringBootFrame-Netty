package com.bcd.protocol.gb32960.datahandler.impl;

import com.bcd.parser.impl.gb32960.data.Packet;
import com.bcd.parser.impl.gb32960.data.PacketData;
import com.bcd.protocol.gb32960.datahandler.DataHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

@Component
public class VehicleValidateTimeHandler extends DataHandler<Packet> {
    public VehicleValidateTimeHandler() {
        super(0x08);
    }

    @Override
    public void handle(Packet data,ChannelHandlerContext ctx) {
        logger.info("receive data vin[{}]",data.getVin());
    }

}
