package com.cds.iot.data.entity;

import java.io.Serializable;

public class Device implements Serializable {

    /**
     * id : 1
     * code : 13298798137191212 设备编码
     * name : 手表1代   设备名称
     * icon_url：场景图标
     * img_url：场景背景图
     * online: 是否在线
     * type_id: 设备类型id
     * is_admin: 是否是管理员
     * state: 0 解除设备，1等待同意，2,拒绝添加，3撤销申请，4添加成功
     * online: 是否在线
     */

    private String id;

    private String code;

    private String name;

    private String scene_id;

    private String type_id;

    private String main_img_url;

    private String add_icon_url;

    private String img_url;

    private String is_admin;

    private String  state;

    private String online;

    private String admin_name;

    private Object tag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScene_id() {
        return scene_id;
    }

    public void setScene_id(String scene_id) {
        this.scene_id = scene_id;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getMain_img_url() {
        return main_img_url;
    }

    public void setMain_img_url(String main_img_url) {
        this.main_img_url = main_img_url;
    }

    public String getAdd_icon_url() {
        return add_icon_url;
    }

    public void setAdd_icon_url(String add_icon_url) {
        this.add_icon_url = add_icon_url;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(String is_admin) {
        this.is_admin = is_admin;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getAdmin_name() {
        return admin_name;
    }

    public void setAdmin_name(String admin_name) {
        this.admin_name = admin_name;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }
}
