package com.cyf.Netty.login.client;

import com.cyf.Netty.login.protocol.command.Packet;
import com.cyf.Netty.login.protocol.command.PacketCodeC;
import com.cyf.Netty.login.protocol.request.LoginRequestPacket;
import com.cyf.Netty.login.protocol.response.LoginResponsePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.UUID;

/**
 * 客户端数据服务
 * @Author cyfIverson
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端开始登录");
        //创建登录对象
        LoginRequestPacket loginRequestPacket = new LoginRequestPacket();
        loginRequestPacket.setUserId(UUID.randomUUID().toString());
        loginRequestPacket.setUserName("Allen");
        loginRequestPacket.setPassword("666666");

        //编码
        ByteBuf byteBuf = PacketCodeC.INSTANCE.encode(loginRequestPacket);
        //写数据
        ctx.channel().writeAndFlush(byteBuf);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf byteBuf = (ByteBuf) msg;
        Packet packet = PacketCodeC.INSTANCE.decode(byteBuf);

        if (packet instanceof LoginResponsePacket){
            LoginResponsePacket loginResponsePacket = (LoginResponsePacket) packet;
            if (loginResponsePacket.isSuccess()){
                System.out.println("登录成功");
            }else {
                System.out.println("登录失败:"+loginResponsePacket.getMessage());
            }
        }
    }
}
