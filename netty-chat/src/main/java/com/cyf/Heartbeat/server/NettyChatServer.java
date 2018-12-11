package com.cyf.Heartbeat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;

/**
 * 关于心跳机制详解
 * @Author cyfIverson
 * @Date 2018-12-09
 */
public class NettyChatServer {

    public static void main(String[] args) {
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(boosGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                //日志打印的处理Handler
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new IdleStateHandler(5,7,10, TimeUnit.SECONDS));

                        pipeline.addLast(new ServerHandler());
                    }
                });

        bind(serverBootstrap,8099);
    }

    /**
     * 服务启动绑定端口
     * @param serverBootstrap 服务启动引导类
     * @param bindPost 绑定端口号
     */
    public static void bind(ServerBootstrap serverBootstrap,int bindPost){
        serverBootstrap.bind(bindPost).addListener(future -> {
            if (future.isSuccess()){
                System.out.println("服务端启动成功");
            }else{
                System.out.println("服务端启动失败");
            }
        });
    }
}
