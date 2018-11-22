package com.cds.iot.data.entity;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/17 16:05
 */
public class TrackReq {
    /**
     * date : 2018-10-17
     * device_id : 2
     * user_id : 31
     */

    private String user_id;
    private String device_id;
    private String date;

    public TrackReq() {
    }

    public TrackReq(String user_id, String device_id, String date) {
        this.user_id = user_id;
        this.device_id = device_id;
        this.date = date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
