package com.bcd.protocol.gb32960.handler;

import com.bcd.protocol.gb32960.data.Packet;
import com.bcd.protocol.gb32960.data.PacketData;
import com.bcd.protocol.gb32960.parse.impl.PacketDataFieldHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unchecked")
@Component
public class PacketContentParseHandler extends ChannelInboundHandlerAdapter {
    Logger logger= LoggerFactory.getLogger(PacketContentParseHandler.class);

    @Autowired
    PacketDataFieldHandler packetDataFieldHandler;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Packet packet=(Packet)msg;
        PacketData packetData= packetDataFieldHandler.handle(Unpooled.wrappedBuffer(packet.getDataContent()),packet);
        packet.setData(packetData);
        super.channelRead(ctx, msg);
    }


}
