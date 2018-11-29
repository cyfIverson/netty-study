package com.cyf.Netty.communication.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.nio.charset.Charset;

/**
 * 服务端数据处理
 * @Author cyfIverson
 */
public class FirstServerHandler extends ChannelInboundHandlerAdapter{

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //接收数据
        ByteBuf buffer = (ByteBuf) msg;
        System.out.println("服务端读到的数据："+buffer.toString(Charset.forName("utf-8")));

        //回复客户端数据
        ByteBuf out = getByteBuf(ctx);
        ctx.channel().writeAndFlush(out);
    }

    public ByteBuf getByteBuf(ChannelHandlerContext ctx){

        byte[] bytes = "hello client，server receive data".getBytes(Charset.forName("utf-8"));
        ByteBuf byteBuf = ctx.alloc().buffer();
        byteBuf.writeBytes(bytes);
        return byteBuf;
    }
}
