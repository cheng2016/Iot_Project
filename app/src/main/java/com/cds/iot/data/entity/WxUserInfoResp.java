package com.cds.iot.data.entity;

import java.io.Serializable;
import java.util.List;

public class WxUserInfoResp extends GetWXError implements Serializable {
    /**
     * openid : oIepsuNNeLEOztd3BND9XvmIdeOQ
     * nickname : 1994
     * sex : 1
     * language : zh_CN
     * city : Shenzhen
     * province : Guangdong
     * country : CN
     * headimgurl : http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTI4lQqXdFkO0vJ46IlE9vllI7jdn5nqfBLEfNOia6OFwaSFdbBskAf0UClxXoJxPECmtsrOKlQeJqA/132
     * privilege : []
     * unionid : o_VMus7MQV_KD_g7TI5SpLssJrKI
     */

    private String openid;
    private String nickname;
    private int sex;
    private String language;
    private String city;
    private String province;
    private String country;
    private String headimgurl;
    private String unionid;
    private List<?> privilege;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public List<?> getPrivilege() {
        return privilege;
    }

    public void setPrivilege(List<?> privilege) {
        this.privilege = privilege;
    }
}
