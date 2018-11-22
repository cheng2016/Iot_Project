package com.cds.iot.data.entity;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/11/14 17:02
 */
public class LoginOut {
    private String logout;

    public LoginOut(String logout) {
        this.logout = logout;
    }

    public String getLogout() {
        return logout;
    }

    public void setLogout(String logout) {
        this.logout = logout;
    }
}
