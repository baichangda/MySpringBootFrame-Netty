package com.bcd.protocol.gb32960.handler;

import com.bcd.nettyserver.tcp.parse.Parser;
import com.bcd.protocol.gb32960.data.Packet;
import com.bcd.nettyserver.tcp.util.ParseUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@SuppressWarnings("unchecked")
@Component
@ChannelHandler.Sharable
public class PacketParseHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    @Qualifier("parser_32960")
    Parser parser;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //1、读取数据
        ByteBuf byteBuf=(ByteBuf)msg;
        //2、解析成16进制报文
        byte[] content=new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(0,content);
        String hex= ParseUtil.toHex(content);
        //3、解析成实体
        Packet packet= parser.parse(Packet.class,byteBuf);
        packet.setHex(hex);
        super.channelRead(ctx, packet);
    }
}
