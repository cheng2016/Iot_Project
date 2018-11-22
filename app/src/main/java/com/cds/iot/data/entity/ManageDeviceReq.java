package com.cds.iot.data.entity;

/**
 * @author Chengzj
 * @date 2018/9/20 15:51
 */
public class ManageDeviceReq {

    /**
     * user_id ：用户Id
     * device_id : 设备ID
     * update_user_id : id [array]
     * update_type : 操作类型 0 解除设备，1同意添加，2,拒绝添加，3撤销申请
     */
    private String uid;
    private String user_id;
    private String[] update_user_id;
    private String device_id;
    private String update_type;

    public ManageDeviceReq(String uid, String device_id) {
        this.uid = uid;
        this.device_id = device_id;
    }

    public ManageDeviceReq(String user_id, String[] update_user_id, String device_id, String update_type) {
        this.user_id = user_id;
        this.update_user_id = update_user_id;
        this.device_id = device_id;
        this.update_type = update_type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String[] getUpdate_user_id() {
        return update_user_id;
    }

    public void setUpdate_user_id(String[] update_user_id) {
        this.update_user_id = update_user_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getUpdate_type() {
        return update_type;
    }

    public void setUpdate_type(String update_type) {
        this.update_type = update_type;
    }
}
