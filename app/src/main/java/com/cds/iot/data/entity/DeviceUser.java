package com.cds.iot.data.entity;

import java.io.Serializable;

/**
 * @author Chengzj
 * @date 2018/9/25 16:00
 */
public class DeviceUser implements Serializable {
    /**
     * is_admin : 1
     * img_url : http://sit.wecarelove.com/api/files/files/getImg?content=%7B%22file_name%22%3A%22%2Fusr%2Fdata%2Fopt%2Fappheand%2F20180813%2F24%2F20180813142830-24.png%22%2C%22name%22%3A%2220180813142830-24.png%22%7D
     * name : 新用户
     * id : 24
     */

    private String is_admin;
    private String img_url;
    private String name;
    private String phone_number;
    private int id;

    public String getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(String is_admin) {
        this.is_admin = is_admin;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
