package com.cds.iot.data.entity;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/11/13 11:03
 */
public class DeviceCodeReq {

    /**
     * qrcode_url : 二维码路径
     */

    private String qrcode_url;

    public DeviceCodeReq(String qrcode_url) {
        this.qrcode_url = qrcode_url;
    }

    public String getQrcode_url() {
        return qrcode_url;
    }

    public void setQrcode_url(String qrcode_url) {
        this.qrcode_url = qrcode_url;
    }
}
