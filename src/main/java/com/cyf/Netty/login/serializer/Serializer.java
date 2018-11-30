package com.cyf.Netty.login.serializer;

import com.cyf.Netty.login.serializer.impl.JSONSerializer;

/**
 * 序列化接口(编码解码)
 * @Author cyfIverson
 */
public interface Serializer {

    Serializer DEFAULT = new JSONSerializer();

    /**
     * 序列化算法
     */
    byte getSerializerAlgorithm();

    /**
     * Java对象转换成二进制
     */
    byte[] serialize(Object object);

    /**
     * 二进制转换成 java 对象
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
