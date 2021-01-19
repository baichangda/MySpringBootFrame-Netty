package com.bcd.protocol.gb32960.datahandler.impl;

import com.bcd.parser.impl.gb32960.data.Packet;
import com.bcd.parser.impl.gb32960.data.VehicleLoginData;
import com.bcd.protocol.gb32960.datahandler.DataHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unchecked")
public class VehicleLoginDataHandler extends DataHandler<Packet> {
    public VehicleLoginDataHandler() {
        super(0x01);
    }

    @Override
    public void handle(Packet data,ChannelHandlerContext ctx) {
        logger.info("receive data vin[{}]",data.getVin());
    }

}
