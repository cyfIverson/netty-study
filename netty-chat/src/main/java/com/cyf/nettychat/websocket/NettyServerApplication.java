package com.cyf.nettychat.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * nettyServer
 * @Author cyfIverson
 */
@Configuration
@ChannelHandler.Sharable
public class NettyServerApplication {

    /** boos线程组 */
    private NioEventLoopGroup boosGroup(){
        return new NioEventLoopGroup();
    }

    /** worker线程组 */
    private NioEventLoopGroup workerGroup(){
        return new NioEventLoopGroup();
    }

    /** 聊天室处理 */
    @Autowired
    private ChatHandler chatHandler;

    @Bean(name = "serverBootstrap")
    public ServerBootstrap serverBootstrap(){
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(boosGroup(),workerGroup())
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,1024)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .childHandler(new ChannelInitializer<SocketChannel >() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new HttpServerCodec());
                        socketChannel.pipeline().addLast(new ChunkedWriteHandler());
                        socketChannel.pipeline().addLast(new HttpObjectAggregator(64 * 1024));
                        socketChannel.pipeline().addLast(new WebSocketServerProtocolHandler("/webSocket"));
                        socketChannel.pipeline().addLast(chatHandler);
                    }
                });
        return serverBootstrap;
    }
}
