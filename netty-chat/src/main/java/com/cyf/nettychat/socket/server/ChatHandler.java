package com.cyf.nettychat.socket.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 服务逻辑处理Handler
 * @Author cyfIverson
 * @Date 2018-12-09
 */
public class ChatHandler extends SimpleChannelInboundHandler<String> {

    //利用netty的ChannelGroup来存储channel
    private static DefaultChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();
        //服务广播时，区分当前channel和其他channel的处理
        /*channelGroup.forEach(channel1 -> {
            if (channel != channel1){
                channel1.writeAndFlush(channel.remoteAddress()+"发送消息:"+msg);
            }else {
                channel1.writeAndFlush("[自己]发送信息"+msg);
            }
        });*/
        for (Channel ch : channelGroup){
            if (ch != channel){
                ch.writeAndFlush(channel.remoteAddress()+"发送消息:"+msg+"\n");
            }else {
                ch.writeAndFlush("[自己]发送信息"+msg+"\n");
            }
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[服务器]"+channel.remoteAddress()+"加入\n");
        channelGroup.add(channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[服务器]"+channel.remoteAddress()+"离开\n");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress()+"上线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress()+"下线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
