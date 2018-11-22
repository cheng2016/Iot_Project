package com.cds.iot.data.entity;

import java.io.Serializable;
import java.util.List;

public class DeviceType implements Serializable{
    private String id;
    private String name;
    private String icon_url;
    private String add_icon_url;

    private List<Device> devices;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getAdd_icon_url() {
        return add_icon_url;
    }

    public void setAdd_icon_url(String add_icon_url) {
        this.add_icon_url = add_icon_url;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }
}
