package com.cyf.Netty.login.protocol.response;

import com.cyf.Netty.login.protocol.command.Packet;
import lombok.Data;

/**
 * 登录响应消息对象
 * @Author cyfIverson
 */
@Data
public class LoginResponsePacket extends Packet {

    private boolean success;
    private String message;

    @Override
    public Byte getCommand() {
        return null;
    }
}
