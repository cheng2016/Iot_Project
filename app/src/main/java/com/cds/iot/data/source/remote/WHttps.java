package com.cds.iot.data.source.remote;

import com.cds.iot.data.entity.WetherInfo;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 获取天气接口
 */
public interface WHttps {
    public static final String wetherBaseurl = "https://restapi.amap.com/v3/weather/";

    @GET("weatherInfo")
    Observable<WetherInfo> getWeatherInfo(@Query("key") String key,
                                          @Query("city") String city);
}
