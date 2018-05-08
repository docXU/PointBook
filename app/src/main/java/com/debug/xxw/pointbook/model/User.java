package com.debug.xxw.pointbook.model;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String headimg;
    private String email;
    private String sex;
    private int age;
    private String address;
    private String telephone;
    private String wechat_id;
    private String weibo_name;
    private String describe;

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getHeadimg() {
        return headimg;
    }

    public User setHeadimg(String headimg) {
        this.headimg = headimg;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getSex() {
        return sex;
    }

    public User setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public int getAge() {
        return age;
    }

    public User setAge(int age) {
        this.age = age;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public User setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getTelephone() {
        return telephone;
    }

    public User setTelephone(String telephone) {
        this.telephone = telephone;
        return this;
    }

    public String getWechat_id() {
        return wechat_id;
    }

    public User setWechat_id(String wechat_id) {
        this.wechat_id = wechat_id;
        return this;
    }

    public String getWeibo_name() {
        return weibo_name;
    }

    public User setWeibo_name(String weibo_name) {
        this.weibo_name = weibo_name;
        return this;
    }

    public String getDescribe() {
        return describe;
    }

    public User setDescribe(String describe) {
        this.describe = describe;
        return this;
    }
}
