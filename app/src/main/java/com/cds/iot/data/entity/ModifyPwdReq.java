package com.cds.iot.data.entity;

public class ModifyPwdReq {
    /**
     * old_pwd : 旧密码
     * new_pwd : 新密码
     * user_id : 用户id
     */

    private String old_pwd;
    private String new_pwd;
    private int user_id;

    public ModifyPwdReq(String old_pwd, String new_pwd, int user_id) {
        this.old_pwd = old_pwd;
        this.new_pwd = new_pwd;
        this.user_id = user_id;
    }

    public String getOld_pwd() {
        return old_pwd;
    }

    public void setOld_pwd(String old_pwd) {
        this.old_pwd = old_pwd;
    }

    public String getNew_pwd() {
        return new_pwd;
    }

    public void setNew_pwd(String new_pwd) {
        this.new_pwd = new_pwd;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
