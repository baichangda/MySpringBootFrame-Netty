package com.bcd.protocol.gb32960.datahandler.impl;

import com.bcd.parser.impl.gb32960.data.Packet;
import com.bcd.parser.impl.gb32960.data.PlatformLogoutData;
import com.bcd.protocol.gb32960.datahandler.DataHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

@Component
public class PlatformLogoutDataHandler extends DataHandler<Packet> {
    public PlatformLogoutDataHandler() {
        super(0x06);
    }

    @Override
    public void handle(Packet data,ChannelHandlerContext ctx) {
        logger.info("receive data vin[{}]",data.getVin());
    }

}
