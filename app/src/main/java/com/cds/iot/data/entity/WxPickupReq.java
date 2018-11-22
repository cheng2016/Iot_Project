package com.cds.iot.data.entity;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/11 11:45
 */
public class WxPickupReq {
    /**
     * device_id : 设备id
     */

    private String device_id;

    public WxPickupReq(String device_id) {
        this.device_id = device_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
}
