package com.cds.iot.data.entity;

import java.util.List;

public class TelphoneInfo {


    /**
     * is_admin : 1
     * file_url : http://sit.wecarelove.com/api/files/files/getRecord?content=%7B%22file_name%22%3A%22%2Fusr%2Fdata%2Fopt%2F20180930%2F31%2F20180930114745-31.wav%22%7D
     * alarm_type : 120
     * contact : [{"phone":"13691912026","name":"倪安","id":38},{"phone":"13691912026","name":"倪安","id":39},{"phone":"13691912026","name":"倪安","id":41},{"phone":"13691912026","name":"倪安","id":42},{"phone":"13691912026","name":"倪安","id":43}]
     * nickname :
     * record_duration : 10
     * message : sadf
     */

    private String is_admin;
    private String file_url;
    private String alarm_type;
    private String nickname;
    private String record_duration;
    private String message;
    private List<ContactBean> contact;

    public String getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(String is_admin) {
        this.is_admin = is_admin;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public String getAlarm_type() {
        return alarm_type;
    }

    public void setAlarm_type(String alarm_type) {
        this.alarm_type = alarm_type;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRecord_duration() {
        return record_duration;
    }

    public void setRecord_duration(String record_duration) {
        this.record_duration = record_duration;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ContactBean> getContact() {
        return contact;
    }

    public void setContact(List<ContactBean> contact) {
        this.contact = contact;
    }
}
