package com.cyf.serializable;

import java.io.*;

/**
 * 测试对象序列化和反序列化
 * @Author cyfIverson
 * @Date 2019-02-19
 */
public class TestObjSerializable {

    public static void main(String[] args) throws Exception {
        SerializablePerson();
        Person person = DeSerializablePerson();
        System.out.println(person.toString());
    }

    /**
     * 序列化Person对象
     */
    public static void SerializablePerson() throws IOException {
        Person person = new Person();
        person.setAge(8);
        person.setName("allen");
        person.setSex("nan");

        ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(new File("E:/Person.txt")));
        oo.writeObject(person);
        System.out.println("对象序列化成功");
        oo.close();
    }

    /**
     * 反序列化Person对象
     */
    public static Person DeSerializablePerson() throws Exception {
        ObjectInputStream ii = new ObjectInputStream(new FileInputStream(new File("E:/Person.txt")));
        Person person = (Person) ii.readObject();
        System.out.println("对象反序列化成功");
        return person;
    }
}
