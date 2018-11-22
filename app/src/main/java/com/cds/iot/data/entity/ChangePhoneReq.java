package com.cds.iot.data.entity;

public class ChangePhoneReq {
    /**
     * new_phone_number : 新手机号
     * code : 新手机号验证码
     * user_id : 用户ID
     */

    private String new_phone_number;
    private String code;
    private int user_id;

    public ChangePhoneReq(String new_phone_number, String code, int user_id) {
        this.new_phone_number = new_phone_number;
        this.code = code;
        this.user_id = user_id;
    }

    public String getNew_phone_number() {
        return new_phone_number;
    }

    public void setNew_phone_number(String new_phone_number) {
        this.new_phone_number = new_phone_number;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
