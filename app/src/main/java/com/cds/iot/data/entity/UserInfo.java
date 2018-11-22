package com.cds.iot.data.entity;

import java.io.Serializable;

public class UserInfo implements Serializable{

    /**
     * birthday : 1533195665000
     * wechat_state : 0
     * sex : 0
     * nickname : 新用户
     * phone_number : 18202745852
     */
    private int user_id;
    private long birthday;
    private int wechat_state;
    private int sex;
    private String nickname;
    private String phone_number;
    private String wechat_nickname;
    private String head_img;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public int getWechat_state() {
        return wechat_state;
    }

    public void setWechat_state(int wechat_state) {
        this.wechat_state = wechat_state;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getWechat_nickname() {
        return wechat_nickname;
    }

    public void setWechat_nickname(String wechat_nickname) {
        this.wechat_nickname = wechat_nickname;
    }

    public String getHead_img() {
        return head_img;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }
}
