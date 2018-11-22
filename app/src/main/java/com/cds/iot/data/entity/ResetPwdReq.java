package com.cds.iot.data.entity;

public class ResetPwdReq {

    /**
     * phone_number : 手机号
     * new_pwd : 新密码
     * code : 验证码
     */

    private String phone_number;
    private String new_pwd;
    private String code;

    public ResetPwdReq(String phone_number, String new_pwd, String code) {
        this.phone_number = phone_number;
        this.new_pwd = new_pwd;
        this.code = code;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getNew_pwd() {
        return new_pwd;
    }

    public void setNew_pwd(String new_pwd) {
        this.new_pwd = new_pwd;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
