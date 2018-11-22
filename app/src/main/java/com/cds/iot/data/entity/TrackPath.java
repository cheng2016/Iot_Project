package com.cds.iot.data.entity;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/17 16:19
 */
public class TrackPath {
    /**
     * address : 广东省深圳市福田区沙头街道滨河大道辅道京基滨河时代广场
     * location : 114.026077745226,22.528822699653
     * time : 1533053646000
     */

    private String address;
    private String location;
    private String time;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
