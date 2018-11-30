package com.cyf.Netty.login.server;

import com.cyf.Netty.login.protocol.command.Packet;
import com.cyf.Netty.login.protocol.command.PacketCodeC;
import com.cyf.Netty.login.protocol.request.LoginRequestPacket;
import com.cyf.Netty.login.protocol.response.LoginResponsePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 服务端数据处理
 * @Author cyfIverson
 */
public class ServerHandler extends ChannelInboundHandlerAdapter{

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //接收数据
        System.out.println("客户端开始登录");
        ByteBuf buffer = (ByteBuf) msg;
        Packet packet = PacketCodeC.INSTANCE.decode(buffer);
        //登录过程处理
        if (packet instanceof LoginRequestPacket){
            LoginRequestPacket loginRequestPacket = (LoginRequestPacket)packet;

            LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
            loginResponsePacket.setVersion(packet.getVersion());
            if (valid(loginRequestPacket)){
                loginResponsePacket.setSuccess(true);
                System.out.println("登录成功");
            }else {
                System.out.println("登录失败");
                loginResponsePacket.setSuccess(false);
                loginResponsePacket.setMessage("账号或密码错误");
            }
            //登录响应
            ByteBuf responseByteBuf = PacketCodeC.INSTANCE.encode(loginResponsePacket);
            ctx.channel().writeAndFlush(responseByteBuf);
        }
    }

    /**
     * 校验客户端登录
     * @param loginRequestPacket 登录的对象
     * @return 成功：true 失败：false
     */
    private boolean valid(LoginRequestPacket loginRequestPacket) {
        return true;
    }
}
