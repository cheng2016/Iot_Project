package com.cds.iot.data.entity;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/11 14:53
 */
public class WxPickupResp {
    /**
     * sub_title :
     * img_url : http://mmbiz.qpic.cn/mmbiz_png/1TY3s3P3GlbKJvPQBJ4zZ5G8CS8AWr0bjgCBL2sSKzLZFbEOC2TRv8XHkq5ajg1Wj0d9lEarNnaicMYTzxnZROA/0
     * title : 微信接人
     * url : http://sit.wecarelove.com/api/open/navigation/pickup?pickUp=1&deviceId=9&sendTime=1539240737432
     */

    private String sub_title;
    private String img_url;
    private String title;
    private String content;
    private String url;

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
