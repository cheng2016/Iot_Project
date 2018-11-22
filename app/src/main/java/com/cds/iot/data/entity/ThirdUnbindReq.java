package com.cds.iot.data.entity;

/**
 * @author Chengzj
 * @date 2018/9/27 9:51
 */
public class ThirdUnbindReq {
    /**
     * user_id : 用户id
     * login_type : 登录类型
     */

    private String user_id;
    private int login_type;

    public ThirdUnbindReq() {
    }

    public ThirdUnbindReq(String user_id, int login_type) {
        this.user_id = user_id;
        this.login_type = login_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getLogin_type() {
        return login_type;
    }

    public void setLogin_type(int login_type) {
        this.login_type = login_type;
    }
}
