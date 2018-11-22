package com.cds.iot.data.entity;

/**
 * @Author: chengzj
 * @CreateDate: 2018/11/21 16:26
 * @Version: 3.0.0
 */
public class AlarmReq {
    /**
     * alarm_id : 设备id
     */

    private String alarm_id;

    public AlarmReq(String alarm_id) {
        this.alarm_id = alarm_id;
    }

    public String getAlarm_id() {
        return alarm_id;
    }

    public void setAlarm_id(String alarm_id) {
        this.alarm_id = alarm_id;
    }
}
