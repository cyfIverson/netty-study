package com.cyf.serializable;

import java.io.Serializable;

/**
 * 测试对象序列化和反序列化Person
 * @Author cyfIverson
 * @Date 2019-02-19
 */
public class Person implements Serializable{

    /** 序列化版本号 */
    private static final long serialVersionUID = -5809782578272943999L;

    /** 年龄 */
    private int age;
    /** 姓名 */
    private String name;
    /** 性别 */
    private String sex;

    /** 获取 年龄 */
    public int getAge() {
        return this.age;
    }

    /** 设置 年龄 */
    public void setAge(int age) {
        this.age = age;
    }

    /** 获取 姓名 */
    public String getName() {
        return this.name;
    }

    /** 设置 姓名 */
    public void setName(String name) {
        this.name = name;
    }

    /** 获取 性别 */
    public String getSex() {
        return this.sex;
    }

    /** 设置 性别 */
    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "Person{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }
}
