package com.cds.iot.data.entity;

public class ContactBean {
    private Integer id;
    private String name;
    private String phone;

    public ContactBean() {
    }

    public ContactBean(String name,String phone) {
        this.name = name;
        this.phone = phone;
    }

    public ContactBean(Integer id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
