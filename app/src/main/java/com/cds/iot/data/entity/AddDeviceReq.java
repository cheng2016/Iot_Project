package com.cds.iot.data.entity;

public class AddDeviceReq {

    /**
     * device_code : 设备编码
     * user_id : 用户ID
     * device_name : 设备名（非必填）
     */

    private String device_code;
    private String device_name;
    private int user_id;



    public AddDeviceReq() {
    }

    public AddDeviceReq(String device_code, String device_name, int user_id) {
        this.device_code = device_code;
        this.device_name = device_name;
        this.user_id = user_id;
    }

    public String getDevice_code() {
        return device_code;
    }

    public void setDevice_code(String device_code) {
        this.device_code = device_code;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }
}
