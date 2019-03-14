package com.bcd.protocol.gb32960.datahandler.impl;

import com.bcd.protocol.gb32960.data.PlatformLoginData;
import com.bcd.protocol.gb32960.datahandler.DataHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

@Component
public class PlatformLoginDataHandler extends DataHandler<PlatformLoginData> {
    public PlatformLoginDataHandler() {
        super(0x05);
    }

    @Override
    public void handle(PlatformLoginData data,ChannelHandlerContext ctx) {
    }

}
