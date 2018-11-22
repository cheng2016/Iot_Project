package com.cds.iot.data.entity;

public class GetCodeReq {
    /**
     * phone_number : 手机号
     */

    private String phone_number;

    public GetCodeReq(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
