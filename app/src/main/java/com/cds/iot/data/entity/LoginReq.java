package com.cds.iot.data.entity;

public class LoginReq {

    /**
     * phone_number : 手机号
     * login_pwd : 密码
     */

    private String phone_number;
    private String login_pwd;

    public LoginReq(String phone_number, String login_pwd) {
        this.phone_number = phone_number;
        this.login_pwd = login_pwd;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getLogin_pwd() {
        return login_pwd;
    }

    public void setLogin_pwd(String login_pwd) {
        this.login_pwd = login_pwd;
    }
}
