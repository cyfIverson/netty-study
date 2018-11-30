package com.cyf.Netty.login.serializer.impl;

import com.alibaba.fastjson.JSON;
import com.cyf.Netty.login.serializer.Serializer;
import com.cyf.Netty.login.serializer.SerializerAlgorithm;

/**
 *
 * @Author cyfIverson
 */
public class JSONSerializer implements Serializer {

    /**
     * 序列化算法
     */
    @Override
    public byte getSerializerAlgorithm() {
        return SerializerAlgorithm.JSON;
    }

    /**
     * Java对象转换成二进制
     */
    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object);
    }

    /**
     * 二进制转换成 java 对象
     */
    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes, clazz);
    }
}
