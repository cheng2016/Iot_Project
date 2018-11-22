package com.cds.iot.data.entity;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/11/13 9:37
 */
public class MessageType {
    /**
     * message_type_name : 后视镜
     * message_tyep_id : 1
     */

    private String message_type_name;
    private int message_tyep_id;

    public String getMessage_type_name() {
        return message_type_name;
    }

    public void setMessage_type_name(String message_type_name) {
        this.message_type_name = message_type_name;
    }

    public int getMessage_tyep_id() {
        return message_tyep_id;
    }

    public void setMessage_tyep_id(int message_tyep_id) {
        this.message_tyep_id = message_tyep_id;
    }
}
