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
public class TcpServer_32960 extends TcpServer{

    @Autowired
    PacketParseHandler packetParseHandler;

    @Autowired
    BusinessHandler businessHandler;

    public TcpServer_32960(@Value("${server.gb32960.port}")int port){
        super(port);
    }

    public void run(){
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
