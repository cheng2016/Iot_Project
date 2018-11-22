package com.cds.iot.data.entity;

/**
 * @Author: chengzj
 * @CreateDate: 2018/11/21 15:56
 * @Version: 3.0.0
 */
public class SaveAlarmReq {
    private String device_id;
    private int record_is_modify;
    private String alarm_id;
    private String alarm_time;
    private String week;
    private String title;
    private int state;

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public int getRecord_is_modify() {
        return record_is_modify;
    }

    public void setRecord_is_modify(int record_is_modify) {
        this.record_is_modify = record_is_modify;
    }

    public String getAlarm_time() {
        return alarm_time;
    }

    public void setAlarm_time(String alarm_time) {
        this.alarm_time = alarm_time;
    }

    public String getAlarm_id() {
        return alarm_id;
    }

    public void setAlarm_id(String alarm_id) {
        this.alarm_id = alarm_id;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
