package com.cds.iot.data.entity;

public class RegisterReq {

    /**
     * phone_number : 手机号
     * pwd : 密码
     * code : 验证码
     */

    private String phone_number;
    private String pwd;
    private String code;

    public RegisterReq() {
    }

    public RegisterReq(String phone_number, String pwd, String code) {
        this.phone_number = phone_number;
        this.pwd = pwd;
        this.code = code;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
