package com.bcd.nettyserver.http;


import com.bcd.nettyserver.http.handler.NettyHttpRequestHandler;
import com.bcd.nettyserver.http.support.spring.converter.SpringConverter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 基于Netty的Http监听服务
 */
public class NettyHttpServer {
    private String serverId;
    private int port;

    public NettyHttpServer(String serverId, int port) {
        this.serverId = serverId;
        this.port = port;
    }

    public void run(){
        EventLoopGroup boosGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        ExecutorService es= Executors.newSingleThreadExecutor();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            SpringConverter springConverter=new SpringConverter();
            serverBootstrap.group(boosGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(
                    new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new ReadTimeoutHandler(30));
                            ch.pipeline().addLast(new HttpServerCodec());
                            ch.pipeline().addLast(new HttpObjectAggregator(1048576));
                            ch.pipeline().addLast(new HttpContentCompressor());
                            ch.pipeline().addLast(new NettyHttpRequestHandler(serverId,springConverter));
                        }
                    }
            );
            ChannelFuture channelFuture= serverBootstrap.bind(new InetSocketAddress(port)).sync();
            channelFuture.channel().closeFuture().sync();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            es.shutdown();
        }
    }
}
