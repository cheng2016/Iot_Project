package com.cds.iot.data.entity;

public class ValidatedeviceReq {

    /**
     * device_code : 设备编码
     * device_type_id : 设备类型id
     */

    private String user_id;
    private String device_code;
    private String device_type_id;

    public ValidatedeviceReq() {
    }

    public ValidatedeviceReq(String user_id, String device_code, String device_type_id) {
        this.user_id = user_id;
        this.device_code = device_code;
        this.device_type_id = device_type_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDevice_code() {
        return device_code;
    }

    public void setDevice_code(String device_code) {
        this.device_code = device_code;
    }

    public String getDevice_type_id() {
        return device_type_id;
    }

    public void setDevice_type_id(String device_type_id) {
        this.device_type_id = device_type_id;
    }
}
