package com.cyf.nettychat.socket.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 向服务端发送消息客户端
 * @Author cyfIverson
 * @Date 2018-12-09
 */
public class NettyChatClient {

    public static void main(String[] args) throws Exception {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        //编码解码Handler
                        pipeline.addLast(new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter()));
                        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                        //服务端处理Handler
                        pipeline.addLast(new ChatClientHandler());
                    }
                });

        Channel channel = bootstrap.connect("localhost", 8099).sync().channel();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        for(;;){
            channel.writeAndFlush(bufferedReader.readLine()+"\r\n");
        }
    }
}