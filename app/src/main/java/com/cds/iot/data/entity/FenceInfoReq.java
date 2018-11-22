package com.cds.iot.data.entity;

/**
 * @author Chengzj
 * @date 2018/9/20 15:10
 */
public class FenceInfoReq {

    /**
     * device_id : 设备id
     */

    private String device_id;

    public FenceInfoReq(String device_id) {
        this.device_id = device_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
}
