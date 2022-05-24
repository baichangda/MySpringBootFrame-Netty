package com.bcd.protocol.gb32960;


import com.bcd.nettyserver.tcp.TcpServer;
import com.bcd.protocol.gb32960.handler.BusinessHandler;
import com.bcd.protocol.gb32960.handler.PacketParseHandler;
import com.bcd.nettyserver.tcp.handler.PacketSplitHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 基于Netty的Http监听服务
 */
@Component
public class Server_gb32960 extends TcpServer{

    @Autowired
    PacketParseHandler packetParseHandler;

    @Autowired
    BusinessHandler businessHandler;

    public Server_gb32960(@Value("${server.gb32960.port}")int port){
        super(port);
    }

    public void run(){
        String s="232302fe4c534a4532343036364a4732323935383901011f14090812110c01020101000000031fec0e8026bb45021013dd0000050007381f0701d8cc8c06013e0f20010c0f1701054e01074c070000000000000000000801010e8126bb00600001600f1b0f1b0f190f1a0f1a0f1a0f1a0f180f1a0f1a0f1b0f170f1b0f1b0f1b0f1c0f190f1a0f1a0f1b0f1a0f1a0f1a0f1a0f1a0f1a0f1a0f190f1a0f190f1b0f1a0f1b0f1a0f190f1a0f1b0f1b0f1a0f1b0f1a0f1d0f1a0f1a0f1b0f1c0f1d0f1d0f1c0f1b0f1b0f1c0f1a0f1d0f1d0f1c0f1d0f1d0f1b0f1b0f1a0f200f1d0f1a0f1a0f1a0f1a0f1b0f1b0f1b0f1a0f1c0f1b0f1a0f1a0f1a0f1b0f1e0f1d0f1b0f1c0f1d0f1d0f1d0f1d0f1c0f1b0f1b0f1d0f1c0f1c0f1c0f1c0f1d0f1b0f1d09010100104d4d4d4d4e4d4c4c4d4c4d4d4d4d4d4c2a";
        EventLoopGroup boosGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        ExecutorService es= Executors.newSingleThreadExecutor();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boosGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(
                    new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new ReadTimeoutHandler(60*10));
                            ch.pipeline().addLast(new PacketSplitHandler.Default(new byte[]{35,35},22,2));
                            ch.pipeline().addLast(packetParseHandler);
                            ch.pipeline().addLast(businessHandler);
                        }
                    }
            );
            ChannelFuture channelFuture= serverBootstrap.bind(new InetSocketAddress(port)).sync();
            logger.info("server listen on port[{}]",port);
            channelFuture.channel().closeFuture().sync();
        }catch(Exception e){
            logger.error("run error",e);
        }finally{
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            es.shutdown();
        }
    }
}
