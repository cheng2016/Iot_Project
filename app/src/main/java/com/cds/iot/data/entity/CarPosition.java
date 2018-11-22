package com.cds.iot.data.entity;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/11 12:26
 */
public class CarPosition {
    /**
     * place_memo :
     * device_id : 22
     * latitude :
     * speed :
     * longitude :
     * device_time :
     */

    private String place_memo;
    private String device_id;
    private String latitude;
    private String speed;
    private String longitude;
    private String device_time;

    public String getPlace_memo() {
        return place_memo;
    }

    public void setPlace_memo(String place_memo) {
        this.place_memo = place_memo;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDevice_time() {
        return device_time;
    }

    public void setDevice_time(String device_time) {
        this.device_time = device_time;
    }
}
