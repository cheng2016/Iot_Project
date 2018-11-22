package com.cds.iot.data.entity;

import java.io.Serializable;

/**
 * @Author: ${USER_NAME}
 * @CreateDate: 2018/11/19 14:29
 * @Version: 3.0.0
 */
public class AlarmInfo implements Serializable {
    /**
     * date : 20:00
     * is_send : 0
     * week : 0000000
     * record_file : https://sit.wecarelove.com/opt/20181101/000000000000234/20181101174348-000000000000234.wav
     * id : 88
     * state : 1
     * title : 1232
     * record_duration : 录音时长
     */

    private String date;
    private int is_send;
    private String week;
    private String record_file;
    private String id;
    private int state;
    private String title;
    private String record_duration;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getIs_send() {
        return is_send;
    }

    public void setIs_send(int is_send) {
        this.is_send = is_send;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getRecord_file() {
        return record_file;
    }

    public void setRecord_file(String record_file) {
        this.record_file = record_file;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRecord_duration() {
        return record_duration;
    }

    public void setRecord_duration(String record_duration) {
        this.record_duration = record_duration;
    }
}
