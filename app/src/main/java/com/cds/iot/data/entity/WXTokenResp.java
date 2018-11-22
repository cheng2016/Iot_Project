package com.cds.iot.data.entity;

public class WXTokenResp extends GetWXError{
    /**
     * access_token : 10_EDzIqOcXyTOvh8GqpDxUO7iLP4LtLoxD8yqY4G-5s5IzHxllOAAw8EuQbdtVy6-M_CxtUL5NRc-WZJJ1QNlfrOyW5dr8gcINF2i7sCE8o4E
     * expires_in : 7200
     * openid : oIepsuNNeLEOztd3BND9XvmIdeOQ
     * refresh_token : 10_CpBGripFsjCtzIBESyZTxUdRHcKvAT7B50FGBFcbEO9-NPHEm3yTpiDBbsCGzONgmsuQTLQbxN-0PHBRt0uleEXgexumKJ574b9jve7x5EE
     * scope : snsapi_userinfo
     * unionid : o_VMus7MQV_KD_g7TI5SpLssJrKI
     */

    private String access_token;
    private int expires_in;
    private String openid;
    private String refresh_token;
    private String scope;
    private String unionid;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
}
