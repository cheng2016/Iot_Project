package com.cds.iot.data.entity;

public class DeviceReq {

    /**
     * user_id : 用户ID
     * device_type_id : 设备类型
     * is_bind : 是否绑定 0：否，1：是（非必填）
     */

    private String user_id;
    private String device_type_id;
    private String is_bind;

    public DeviceReq() {
    }

    public DeviceReq(String user_id) {
        this.user_id = user_id;
    }

    public DeviceReq(String user_id, String device_type_id) {
        this.user_id = user_id;
        this.device_type_id = device_type_id;
    }

    public DeviceReq(String user_id, String device_type_id, String is_bind) {
        this.user_id = user_id;
        this.device_type_id = device_type_id;
        this.is_bind = is_bind;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDevice_type_id() {
        return device_type_id;
    }

    public void setDevice_type_id(String device_type_id) {
        this.device_type_id = device_type_id;
    }

    public String getIs_bind() {
        return is_bind;
    }

    public void setIs_bind(String is_bind) {
        this.is_bind = is_bind;
    }
}
