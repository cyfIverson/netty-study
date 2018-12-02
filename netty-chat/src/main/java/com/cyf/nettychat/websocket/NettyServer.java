package com.cyf.nettychat.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 *
 * 聊天室启动服务
 * @Author cyfIverson
 */
@Component
public class NettyServer {

    @Autowired
    @Qualifier("serverBootstrap")
    private ServerBootstrap serverBootstrap;

    private Channel serverChannel;

    public void start() throws InterruptedException {
        System.out.println("服务启动");
        serverChannel = serverBootstrap.bind(8888).sync().channel().closeFuture().sync().channel();
    }

    @PreDestroy
    public void stop() throws Exception {
        serverChannel.close();
        serverChannel.parent().close();
    }
}
