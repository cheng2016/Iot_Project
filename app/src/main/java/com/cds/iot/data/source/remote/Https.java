package com.cds.iot.data.source.remote;


import com.cds.iot.data.entity.WXTokenResp;
import com.cds.iot.data.entity.WxUserInfoResp;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Https {
    public static final String wxBaseurl = "https://api.weixin.qq.com/";

    @GET("sns/oauth2/access_token")
    Observable<WXTokenResp> getWXToken(@Query("appid") String appid,
                                       @Query("secret") String secret,
                                       @Query("code") String code,
                                       @Query("grant_type") String grant_type);

    @GET("sns/userinfo")
    Observable<WxUserInfoResp> getWxUserInfo(@Query("access_token") String access_token,
                                             @Query("openid") String openid);
}
