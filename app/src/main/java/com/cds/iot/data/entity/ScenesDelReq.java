package com.cds.iot.data.entity;

public class ScenesDelReq {

    /**
     * user_scene_id : 场景ID
     */

    private String user_scene_id;

    public ScenesDelReq() {
    }

    public ScenesDelReq(String user_scene_id) {
        this.user_scene_id = user_scene_id;
    }

    public String getUser_scene_id() {
        return user_scene_id;
    }

    public void setUser_scene_id(String user_scene_id) {
        this.user_scene_id = user_scene_id;
    }
}
