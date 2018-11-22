package com.cds.iot.data.entity;

public class UpdateUserInfoReq {
    /**
     * user_id : 用户id
     * head_img : 用户头像（非必填）
     * nickname : 用户昵称（非必填）
     * sex : 性别（非必填）
     * birthday : 生日（非必填）
     */

    private String user_id;
    private String head_img;
    private String nickname;
    private String sex;
    private String birthday;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getHead_img() {
        return head_img;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
