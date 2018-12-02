package com.cyf.nettychat.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Component;

/**
 *
 * 聊天室处理handler
 * @Author cyfIverson
 */
@Component
@ChannelHandler.Sharable
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{

    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        //获取内容
        String chatMessage = msg.text();

        Channel currentChannel = ctx.channel();
        for (Channel channel : channelGroup){
            if (channel == currentChannel){
                channel.writeAndFlush(new TextWebSocketFrame("我自己:"+chatMessage));
            }else{
                channel.writeAndFlush(new TextWebSocketFrame(currentChannel.remoteAddress()+":"+chatMessage));
            }
        }
    }

    //加入
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        for (Channel channel : channelGroup){
            channel.writeAndFlush(new TextWebSocketFrame(ctx.channel().remoteAddress()+":进入聊天室"));
        }
        channelGroup.add(ctx.channel());
    }

    //退出
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        channelGroup.remove(ctx.channel());
        for (Channel channel : channelGroup){
            channel.writeAndFlush(new TextWebSocketFrame(ctx.channel().remoteAddress()+":离开聊天室"));
        }
    }
}
