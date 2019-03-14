package com.bcd.protocol.gb32960.datahandler.impl;

import com.bcd.protocol.gb32960.data.PlatformLogoutData;
import com.bcd.protocol.gb32960.datahandler.DataHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

@Component
public class PlatformLogoutDataHandler extends DataHandler<PlatformLogoutData> {
    public PlatformLogoutDataHandler() {
        super(0x06);
    }

    @Override
    public void handle(PlatformLogoutData data,ChannelHandlerContext ctx) {
    }

}
