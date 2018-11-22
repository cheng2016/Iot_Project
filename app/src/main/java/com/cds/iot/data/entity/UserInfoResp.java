package com.cds.iot.data.entity;

public class UserInfoResp {
    /**
     * user_id : 用户id
     *
     * is_used : 是否使用了新手指引功能，0:未使用，1:已使用
     */

    private int user_id;

    private int is_used;

    public UserInfoResp() {
    }

    public UserInfoResp(int user_id) {
        this.user_id = user_id;
    }

    public UserInfoResp(int user_id, int is_used) {
        this.user_id = user_id;
        this.is_used = is_used;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getIs_used() {
        return is_used;
    }

    public void setIs_used(int is_used) {
        this.is_used = is_used;
    }
}
