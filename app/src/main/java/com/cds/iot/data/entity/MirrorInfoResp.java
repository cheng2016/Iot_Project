package com.cds.iot.data.entity;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/11 12:46
 */
public class MirrorInfoResp {
    /**
     * device_name : dsa
     * device_code : 358732036575476
     * device_type : KJ89
     * device_qrcode : http://sit.wecarelove.com/api/files/files/getImg?content=%7B%22file_name%22%3A%22%2Fusr%2Fdata%2Fopt%2Fqrcode%2F358732036574164_4a5f9cdc8ec1557f0b8fa2456145439c%2F358732036574164_4a5f9cdc8ec1557f0b8fa2456145439c.png%22%2C%22name%22%3A%22%2Fusr%2Fdata%2Fopt%2Fqrcode%2F358732036574164_4a5f9cdc8ec1557f0b8fa2456145439c%2F358732036574164_4a5f9cdc8ec1557f0b8fa2456145439c.png%22%7D
     */

    private String device_name;
    private String device_code;
    private String device_type;
    private String device_qrcode;

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice_code() {
        return device_code;
    }

    public void setDevice_code(String device_code) {
        this.device_code = device_code;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getDevice_qrcode() {
        return device_qrcode;
    }

    public void setDevice_qrcode(String device_qrcode) {
        this.device_qrcode = device_qrcode;
    }
}
