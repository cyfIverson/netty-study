package com.cyf.Netty.process;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

/**
 * server服务端流程
 * @Author cyfIverson
 */
public class NettyServer {

    private static final int PORT = 1024;

    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .attr(AttributeKey.newInstance("serverName"), "nettyServer")
                .childAttr(AttributeKey.newInstance("clientKey"), "clientValue")
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                    }
                });


        /*serverBootstrap.handler(new ChannelInitializer<NioServerSocketChannel>() {
            @Override
            protected void initChannel(NioServerSocketChannel ch) throws Exception {
                System.out.println("服务启动");
            }
        });*/

        bind(serverBootstrap,PORT);
    }

    /**
     * 绑定端口
     * @param serverBootstrap 服务启动引导类
     * @param port 端口
     */
    public static void bind(final ServerBootstrap serverBootstrap,final int port){
        serverBootstrap.bind(PORT).addListener(future -> {
            if (future.isSuccess()){
                System.out.println("端口[" + port + "]绑定成功!");
            }else {
                System.err.println("端口[" + port + "]绑定失败!");
                bind(serverBootstrap,port+1);
            }
        });
    }
}
