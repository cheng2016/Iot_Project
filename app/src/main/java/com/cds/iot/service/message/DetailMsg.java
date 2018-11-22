package com.cds.iot.service.message;

/**
 * @author Chengzj
 * @date 2018/9/28 13:59
 */
public class DetailMsg {
    private String uid;
    private String device_id;
    private String device_name;
    private String device_type_id;
    private String device_type_name;
    private String details;
    private String device_type_img;
    private String url;
    private String first_img_url;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice_type_id() {
        return device_type_id;
    }

    public void setDevice_type_id(String device_type_id) {
        this.device_type_id = device_type_id;
    }

    public String getDevice_type_name() {
        return device_type_name;
    }

    public void setDevice_type_name(String device_type_name) {
        this.device_type_name = device_type_name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDevice_type_img() {
        return device_type_img;
    }

    public void setDevice_type_img(String device_type_img) {
        this.device_type_img = device_type_img;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFirst_img_url() {
        return first_img_url;
    }

    public void setFirst_img_url(String first_img_url) {
        this.first_img_url = first_img_url;
    }
}
