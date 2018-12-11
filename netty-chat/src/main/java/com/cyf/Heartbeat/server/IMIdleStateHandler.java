package com.cyf.Heartbeat.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;

/**
 * 状态处理器
 * @Author cyfIverson
 * @Date 2018-12-11
 */
public class IMIdleStateHandler extends IdleStateHandler{


    private static final int READER_IDLE_TIME = 10;

    public IMIdleStateHandler() {
        super(READER_IDLE_TIME, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {

        System.out.println(READER_IDLE_TIME +"秒内未读到数据，关闭连接");
        ctx.channel().close();
    }
}
