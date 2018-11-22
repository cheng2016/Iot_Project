package com.cds.iot.data.entity;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/11/15 14:52
 */
public class TransferDeviceReq {
    /**
     * device_id : 设备ID
     * update_user_id : 更改的管理员用户id
     */

    public TransferDeviceReq(String device_id, String update_user_id) {
        this.device_id = device_id;
        this.update_user_id = update_user_id;
    }

    private String device_id;
    private String update_user_id;

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getUpdate_user_id() {
        return update_user_id;
    }

    public void setUpdate_user_id(String update_user_id) {
        this.update_user_id = update_user_id;
    }
}
