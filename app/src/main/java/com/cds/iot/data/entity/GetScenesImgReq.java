package com.cds.iot.data.entity;

import java.util.List;

public class GetScenesImgReq {

    /**
     * scene_id : 场景ID
     * device_types : ["id"]
     */

    private String scene_id;
    private List<String> device_types;

    public GetScenesImgReq() {
    }

    public GetScenesImgReq(String scene_id, List<String> device_types) {
        this.scene_id = scene_id;
        this.device_types = device_types;
    }

    public String getScene_id() {
        return scene_id;
    }

    public void setScene_id(String scene_id) {
        this.scene_id = scene_id;
    }

    public List<String> getDevice_types() {
        return device_types;
    }

    public void setDevice_types(List<String> device_types) {
        this.device_types = device_types;
    }
}
