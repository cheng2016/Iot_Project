package com.cds.iot.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by chengzj on 2018/9/26.
 */
@Entity
public class SMessage {
    @Id(autoincrement = true)
    private Long id;

    private String token;

    private String userId;

    private String uid;

    private String msgId;

    private String msgType;

    private String title;

    private String content;

    private String deviceImg;

    private String deviceId;

    private String deviceName;

    private String deviceType;

    private String deviceTypeName;

    private String photoUrl;

    private String videoUrl;

    private String tailtime;

    @Generated(hash = 772709153)
    public SMessage(Long id, String token, String userId, String uid, String msgId,
            String msgType, String title, String content, String deviceImg,
            String deviceId, String deviceName, String deviceType,
            String deviceTypeName, String photoUrl, String videoUrl,
            String tailtime) {
        this.id = id;
        this.token = token;
        this.userId = userId;
        this.uid = uid;
        this.msgId = msgId;
        this.msgType = msgType;
        this.title = title;
        this.content = content;
        this.deviceImg = deviceImg;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.deviceTypeName = deviceTypeName;
        this.photoUrl = photoUrl;
        this.videoUrl = videoUrl;
        this.tailtime = tailtime;
    }

    @Generated(hash = 760968538)
    public SMessage() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMsgId() {
        return this.msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsgType() {
        return this.msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDeviceImg() {
        return this.deviceImg;
    }

    public void setDeviceImg(String deviceImg) {
        this.deviceImg = deviceImg;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceType() {
        return this.deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getTailtime() {
        return this.tailtime;
    }

    public void setTailtime(String tailtime) {
        this.tailtime = tailtime;
    }

    public String getPhotoUrl() {
        return this.photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getVideoUrl() {
        return this.videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getDeviceTypeName() {
        return this.deviceTypeName;
    }

    public void setDeviceTypeName(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }
}
