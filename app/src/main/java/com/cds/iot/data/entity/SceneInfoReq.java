package com.cds.iot.data.entity;

public class SceneInfoReq {

    /**
     * user_id : 用户id
     * type : 查询类型：0查询默认场景，1查询用户添加的场景
     */

    private String user_id;
    private String type;

    public SceneInfoReq() {
    }

    public SceneInfoReq(String user_id, String type) {
        this.user_id = user_id;
        this.type = type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
