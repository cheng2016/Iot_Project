package com.cds.iot.data.entity;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/11 11:44
 */
public class MirrorInfoReq {

    /**
     * device_id : 设备id
     * user_id : 用户id
     * device_name : 设备名称
     */

    private String device_id;
    private String user_id;
    private String device_name;

    public MirrorInfoReq() {
    }

    public MirrorInfoReq(String device_id, String user_id) {
        this.device_id = device_id;
        this.user_id = user_id;
    }

    public MirrorInfoReq(String device_id, String user_id, String device_name) {
        this.device_id = device_id;
        this.user_id = user_id;
        this.device_name = device_name;
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

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }
}
