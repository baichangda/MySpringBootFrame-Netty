package com.bcd.protocol.gb32960.handler;

import com.bcd.support_parser.Parser;
import com.bcd.support_parser.impl.gb32960.data.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@SuppressWarnings("unchecked")
@Component
@ChannelHandler.Sharable
public class PacketParseHandler extends ChannelInboundHandlerAdapter {
    Logger logger= LoggerFactory.getLogger(PacketParseHandler.class);
    @Autowired
    @Qualifier("parser_gb32960")
    Parser parser;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //1、读取数据
        ByteBuf byteBuf=(ByteBuf)msg;
        //2、解析成16进制报文
        byte[] content=new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(0,content);
        String hex= ByteBufUtil.hexDump(content);
        logger.info("receive data:{}",hex);
        //3、解析成实体
        Packet packet= parser.parse(Packet.class,byteBuf);
        super.channelRead(ctx, packet);
    }
}
