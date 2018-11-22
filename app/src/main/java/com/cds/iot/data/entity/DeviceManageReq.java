package com.cds.iot.data.entity;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/11 11:39
 */
public class DeviceManageReq {
    private String device_id;
    private String date;

    public DeviceManageReq(String device_id) {
        this.device_id = device_id;
    }

    public DeviceManageReq(String device_id, String date) {
        this.device_id = device_id;
        this.date = date;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
