package com.cds.iot.data.entity;

import java.util.List;

/**
 * @Author: ${USER_NAME}
 * @CreateDate: 2018/11/19 14:26
 * @Version: 3.0.0
 */
public class AlarmInfoResp {
    /**
     * is_admin : 1
     * device_id : 12
     * alarm : [{"date":"20:00","is_send":0,"week":"0000000","record_file":"https://sit.wecarelove.com/opt/20181101/000000000000234/20181101174348-000000000000234.wav","id":88,"state":1,"title":"1232"}]
     */

    private String is_admin;
    private String device_id;
    private List<AlarmInfo> alarm;

    public String getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(String is_admin) {
        this.is_admin = is_admin;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public List<AlarmInfo> getAlramInfo() {
        return alarm;
    }

    public void setAlramInfo(List<AlarmInfo> alarm) {
        this.alarm = alarm;
    }
}
