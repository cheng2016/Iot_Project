package com.cds.iot.data.entity;

import java.io.Serializable;
import java.util.List;

public class BindScenes implements Serializable {

    private String id;
    private String user_scene_id;
    private String name;
    private String icon_url;
    private String icon_hl_url;
    private String img_url;
    private List<DeviceType> device_types;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_scene_id() {
        return user_scene_id;
    }

    public void setUser_scene_id(String user_scene_id) {
        this.user_scene_id = user_scene_id;
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

    public String getIcon_hl_url() {
        return icon_hl_url;
    }

    public void setIcon_hl_url(String icon_hl_url) {
        this.icon_hl_url = icon_hl_url;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public List<DeviceType> getDevice_types() {
        return device_types;
    }

    public void setDevice_types(List<DeviceType> device_types) {
        this.device_types = device_types;
    }
}
