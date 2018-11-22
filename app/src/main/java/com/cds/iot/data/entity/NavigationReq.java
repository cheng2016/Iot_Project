package com.cds.iot.data.entity;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/11 11:43
 */
public class NavigationReq {
    /**
     * device_id : 设备id
     * pick_up : 微信接人
     * lat : 经度
     * lng : 纬度
     * address : 地址
     * name : 导航地址名称
     * type : 导航地址类型
     */

    private String device_id;
    private String pick_up;
    private String name;
    private String address;
    private String lat;
    private String lng;
    private String type;

    public NavigationReq(String device_id, String pick_up, String name, String address, String lat, String lng) {
        this.device_id = device_id;
        this.pick_up = pick_up;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getPick_up() {
        return pick_up;
    }

    public void setPick_up(String pick_up) {
        this.pick_up = pick_up;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
