package com.cyf.Netty.login.protocol.request;

import com.cyf.Netty.login.protocol.command.Packet;
import lombok.Data;

import static com.cyf.Netty.login.protocol.command.Command.LOGIN_REQUEST;

/**
 * 登录请求对象
 * @Author cyfIverson
 */
@Data
public class LoginRequestPacket extends Packet {

    private String userId;

    private String userName;

    private String password;

    @Override
    public Byte getCommand() {
        return LOGIN_REQUEST;
    }
}
