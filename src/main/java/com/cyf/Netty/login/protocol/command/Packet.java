package com.cyf.Netty.login.protocol.command;

import lombok.Data;

/**
 * 通信过程Java对象
 * @Author cyfIverson
 */
@Data
public abstract class Packet {

    /**
     * 协议版本
     */
    private Byte version = 1;

    public abstract Byte getCommand();
}
