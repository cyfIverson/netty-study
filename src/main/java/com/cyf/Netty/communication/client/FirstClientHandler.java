package com.cyf.Netty.communication.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * 客户端数据服务
 * @Author cyfIverson
 */
public class FirstClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(new Date()+"-客户端发送数据");
        //获取数据
        ByteBuf buffer = getByteBuf(ctx);
        //写数据
        ctx.channel().writeAndFlush(buffer);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("客户端接收到数据："+byteBuf.toString(Charset.forName("utf-8")));
    }

    /**
     * 生成数据
     */
    public ByteBuf getByteBuf(ChannelHandlerContext ctx){
        byte[] bytes = "hello server".getBytes(Charset.forName("utf-8"));

        ByteBuf buffer = ctx.alloc().buffer();

        buffer.writeBytes(bytes);

        return buffer;
    }
}
