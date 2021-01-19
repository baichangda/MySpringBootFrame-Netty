package com.bcd.protocol.gb32960.handler;

import com.bcd.base.util.ExceptionUtil;
import com.bcd.parser.impl.gb32960.data.Packet;
import com.bcd.protocol.gb32960.datahandler.DataHandler;
import com.bcd.protocol.gb32960.define.CommonConst;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;



@SuppressWarnings("unchecked")
@Component
@ChannelHandler.Sharable
public class BusinessHandler extends ChannelInboundHandlerAdapter{
    Logger logger= LoggerFactory.getLogger(BusinessHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Object data= ((Packet)msg).getData();
        DataHandler dataHandler= CommonConst.DATA_HANDLER_ARR[((Packet)msg).getFlag()];
        dataHandler.handle(data,ctx);
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ExceptionUtil.printException(cause);
//        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelRegistered");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelActive");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelInactive");
        super.channelInactive(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelUnregistered");
        super.channelUnregistered(ctx);
    }
}
