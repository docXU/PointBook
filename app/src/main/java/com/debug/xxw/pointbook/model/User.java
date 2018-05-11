package com.debug.xxw.pointbook.model;

import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    private int id;
    private String username;
    private String headimg;
    private String email;
    private String sex;
    private String age;
    private String address;
    private String telephone;
    private String wechat_id;
    private String weibo_name;
    private String describe;

    public int getId() {
        return id;
    }

    public User setId(int id) {
        this.id = id;
        return this;
    }

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

    public String getAge() {
        return age;
    }

    public User setAge(String age) {
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

    public static boolean saveUserSingleton(SharedPreferences sp, User user) {
        try {
            SharedPreferences.Editor editor = sp.edit();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(user);
            String base64Student = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            oos.close();
            return editor.putString("user", base64Student).putLong("expires", new Date().getTime()).commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static User getUserSingleton(SharedPreferences sp) {
        Long expires = sp.getLong("expires", 0);
        //一天过期，避免多机登录修改用户资料时导致数据不同步。
        if (new Date().getTime() - expires > 86400000) {
            return null;
        }

        String studentString = sp.getString("user", "");

        if (studentString.isEmpty()) return null;

        byte[] base64Student = Base64.decode(studentString, Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(base64Student);

        User user = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            user = (User) ois.readObject();
            Log.e("------------->", "" + user.getDescribe() + user.getAge() + user.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * 将服务器返回的json字符串对象解析为User对象
     *
     * @param result
     * @return
     */
    public static User parseResult(Object result) {
        try {
            JSONObject user = new JSONObject((String) result);
            return new User().setUsername(user.getString("username"))
                    .setDescribe(user.getString("describe"))
                    .setAddress(user.getString("address"))
                    .setAge(user.getString("age"))
                    .setEmail(user.getString("email"))
                    .setHeadimg(user.getString("headimg"))
                    .setId(Integer.parseInt(user.getString("id")))
                    .setSex(user.getInt("sex") == 0 ? "女" : "男")
                    .setTelephone(user.getString("telephone"))
                    .setWechat_id(user.getString("wechat_id"))
                    .setWeibo_name(user.getString("weibo_name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
