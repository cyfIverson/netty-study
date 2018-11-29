package com.cyf.Netty.process;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * netty的客户端启动流程
 * @Author cyfIverson
 */
public class NettyClient {

    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                //1.指定线程模型
                .group(group)
                //2.指定IO类型 NIO
                .channel(NioSocketChannel.class)
                //3.IO的处理逻辑
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {

                    }
                });

        //4.建立连接 并添加监听 展示是否连接成功
        bootstrap.connect("127.0.0.1", 8888).addListener(future -> {
            if (future.isSuccess()){
                System.out.println("连接成功");
            }else {
                System.out.println("连接失败");
            }
        });
    }
}
