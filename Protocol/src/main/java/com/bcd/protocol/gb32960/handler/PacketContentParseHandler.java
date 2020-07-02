package com.bcd.protocol.gb32960.handler;

import com.bcd.nettyserver.tcp.parse.FieldParseContext;
import com.bcd.protocol.gb32960.data.Packet;
import com.bcd.protocol.gb32960.data.PacketData;
import com.bcd.protocol.gb32960.parse.impl.PacketDataFieldParser;
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
@ChannelHandler.Sharable
public class PacketContentParseHandler extends ChannelInboundHandlerAdapter {
    Logger logger= LoggerFactory.getLogger(PacketContentParseHandler.class);

    @Autowired
    PacketDataFieldParser packetDataFieldHandler;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Packet packet=(Packet)msg;
        PacketData packetData= packetDataFieldHandler.parse(packet.getDataContent(),packet.getFlag(),packet.getDataContent().readableBytes());
        packet.setData(packetData);
        super.channelRead(ctx, msg);
    }


}
