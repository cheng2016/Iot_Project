package com.cds.iot.data.entity;

import java.util.List;

public class SaveTelphoneReq {


    /**
     * device_id : 设备id
     * user_id : 用户id
     * message : 求助短信
     * record_is_modify : 是否修改录音
     * alarm_type : 报警类型
     * contact : [{"phone":"联系电话","name":"联系人名称"}]
     */

    private String device_id;
    private String user_id;
    private String nickname;
    private String message;
    private String alarm_type;
    private int record_is_modify;
    private List<ContactBean> contact;

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAlarm_type() {
        return alarm_type;
    }

    public void setAlarm_type(String alarm_type) {
        this.alarm_type = alarm_type;
    }

    public int getRecord_is_modify() {
        return record_is_modify;
    }

    public void setRecord_is_modify(int record_is_modify) {
        this.record_is_modify = record_is_modify;
    }

    public List<ContactBean> getContact() {
        return contact;
    }

    public void setContact(List<ContactBean> contact) {
        this.contact = contact;
    }
}
