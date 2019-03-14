package com.bcd.protocol.gb32960.handler;

import com.bcd.protocol.gb32960.data.Packet;
import com.bcd.nettyserver.tcp.util.ParseUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.stereotype.Component;


@ChannelHandler.Sharable
@Component
@SuppressWarnings("unchecked")
public class PacketParseHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //1、读取数据
        ByteBuf byteBuf=(ByteBuf)msg;
        //2、解析成16进制报文
        byte[] content=new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(0,content);
        String hex= ParseUtil.toHex(content);
        //3、解析成实体
        Packet packet= ParseUtil.parse(Packet.class,byteBuf);
        packet.setHex(hex);
        super.channelRead(ctx, packet);
    }
}
