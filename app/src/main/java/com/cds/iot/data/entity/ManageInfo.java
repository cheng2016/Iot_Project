package com.cds.iot.data.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author Chengzj
 * @date 2018/9/21 15:34
 */
public class ManageInfo implements Serializable {
    /**
     * user_bind_history : [{"name":"我","time":"2018-11-14 17:49:41","state":"3","state_value":"已撤销"},{"name":"我","time":"2018-11-13 10:11:58","state":"1","state_value":"已申请"},{"name":"管理员","state":"-1","state_value":"审核中"}]
     * device_id : 62
     * user_name : 不
     * device_users : [{"is_admin":"1","img_url":"","name":"新用户","phone_number":"136****2026","id":48}]
     * user_img_url :
     * device_img_url : http://sit.wecarelove.com/api/files/files/get?content=%7B%22file_name%22%3A%22%2Fapp%2Fbackmirror%2Fmain_icn_houshijing%403x.png%22%7D
     * is_admin : 0
     * device_name : 新设备
     * user_id : 40
     * device_code : 863723040000948
     * device_type_id : 1
     * user_phone : 153****2856
     * state : 3
     */

    private String device_id;
    private String user_name;
    private String user_img_url;
    private String device_img_url;
    private String is_admin;
    private String device_name;
    private String user_id;
    private String device_code;
    private int device_type_id;
    private String user_phone;
    private String state;
    private List<UserBindHistory> user_bind_history;
    private List<DeviceUser> device_users;


    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_img_url() {
        return user_img_url;
    }

    public void setUser_img_url(String user_img_url) {
        this.user_img_url = user_img_url;
    }

    public String getDevice_img_url() {
        return device_img_url;
    }

    public void setDevice_img_url(String device_img_url) {
        this.device_img_url = device_img_url;
    }

    public String getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(String is_admin) {
        this.is_admin = is_admin;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDevice_code() {
        return device_code;
    }

    public void setDevice_code(String device_code) {
        this.device_code = device_code;
    }

    public int getDevice_type_id() {
        return device_type_id;
    }

    public void setDevice_type_id(int device_type_id) {
        this.device_type_id = device_type_id;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<UserBindHistory> getUser_bind_history() {
        return user_bind_history;
    }

    public void setUser_bind_history(List<UserBindHistory> user_bind_history) {
        this.user_bind_history = user_bind_history;
    }

    public List<DeviceUser> getDevice_users() {
        return device_users;
    }

    public void setDevice_users(List<DeviceUser> device_users) {
        this.device_users = device_users;
    }
}
