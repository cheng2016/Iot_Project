package com.cds.iot.data.entity;

import java.io.Serializable;

/**
 * @author Chengzj
 * @date 2018/9/18 13:54
 */
public class FenceInfo implements Serializable{

    /**
     * id : 围栏id
     * device_id : 设备id
     * name : 围栏名称
     * repeat_date : 重复监控时间
     * begin_time : 监控开始时间
     * end_time : 监控结束时间
     * center_location : 监控中心点坐标
     * address : 监控地址
     * radius : 半径
     * enable : 状态
     * type : 1
     */



    private String id;
    private String device_id;
    private String name;
    private String repeat_date;
    private String begin_time;
    private String end_time;
    private String center_location;
    private String address;
    private String radius;
    private String enable;
    private Integer type;

    public FenceInfo() {
    }

    public FenceInfo(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRepeat_date() {
        return repeat_date;
    }

    public void setRepeat_date(String repeat_date) {
        this.repeat_date = repeat_date;
    }

    public String getBegin_time() {
        return begin_time;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getCenter_location() {
        return center_location;
    }

    public void setCenter_location(String center_location) {
        this.center_location = center_location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
