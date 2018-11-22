package com.cds.iot.data.entity;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/11/15 14:05
 */
public class UserBindHistory {
    /**
     * name : 我
     * time : 2018-11-14 17:49:41
     * state : 3
     * state_value : 已撤销
     */

    private String name;
    private String time;
    private String state;
    private String state_value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState_value() {
        return state_value;
    }

    public void setState_value(String state_value) {
        this.state_value = state_value;
    }
}
