package com.cds.iot.data.entity;

public class TelphoneInfoReq {
    private String device_id;

    private String user_id;

    public TelphoneInfoReq(String device_id) {
        this.device_id = device_id;
    }

    public TelphoneInfoReq(String device_id, String user_id) {
        this.device_id = device_id;
        this.user_id = user_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
