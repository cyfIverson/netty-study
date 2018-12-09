package com.cyf.nettychat.socket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * 实现多客户端与服务通信的实例(群聊)
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
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //编码解码Handler
                        pipeline.addLast(new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter()));
                        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                        //服务端处理Handler
                        pipeline.addLast(new ChatHandler());
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
