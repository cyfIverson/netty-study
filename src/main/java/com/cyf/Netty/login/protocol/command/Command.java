package com.cyf.Netty.login.protocol.command;

/**
 * 指令
 * @Author cyfIverson
 */
public interface Command {

    //登录标识
    Byte LOGIN_REQUEST = 1;
    //登录
    Byte LOGIN_RESPONSE = 2;
}
