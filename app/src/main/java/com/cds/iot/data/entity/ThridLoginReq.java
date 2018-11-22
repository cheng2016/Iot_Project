package com.cds.iot.data.entity;

public class ThridLoginReq {
    /**
     * platform_id : 三方平台的唯一标识
     * login_type : 登录类型，0:微信登录
     */


    private String platform_id;
    private int login_type;

    public ThridLoginReq() {
    }

    public ThridLoginReq(String platform_id, int login_type) {
        this.platform_id = platform_id;
        this.login_type = login_type;
    }

    public String getPlatform_id() {
        return platform_id;
    }

    public void setPlatform_id(String platform_id) {
        this.platform_id = platform_id;
    }

    public int getLogin_type() {
        return login_type;
    }

    public void setLogin_type(int login_type) {
        this.login_type = login_type;
    }
}
