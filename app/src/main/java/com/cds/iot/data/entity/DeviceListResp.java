package com.cds.iot.data.entity;

import java.util.List;

/**
 * @author Chengzj
 * @date 2018/9/25 13:39
 */
public class DeviceListResp {
    private String name;
    private int id;
    private List<Device> devices;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }


}
