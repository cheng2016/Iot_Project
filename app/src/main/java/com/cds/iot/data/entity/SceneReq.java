package com.cds.iot.data.entity;

import java.util.List;

public class SceneReq {

    /**
     * user_id : 用户id
     * scene_id : 场景id
     * user_scene_id : 用户场景ID(无为添加场景有为修改场景)
     * scene_name : 场景名称
     * devices : [{"code":"设备编码","id":"设备id","name":"场景名称"}]
     */

    private int user_id;
    private String scene_id;
    private String user_scene_id;
    private String scene_name;
    private List<Device> devices;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getScene_id() {
        return scene_id;
    }

    public void setScene_id(String scene_id) {
        this.scene_id = scene_id;
    }

    public String getUser_scene_id() {
        return user_scene_id;
    }

    public void setUser_scene_id(String user_scene_id) {
        this.user_scene_id = user_scene_id;
    }

    public String getScene_name() {
        return scene_name;
    }

    public void setScene_name(String scene_name) {
        this.scene_name = scene_name;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }
}
