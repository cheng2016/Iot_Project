package com.cds.iot.data.source.remote;

import com.cds.iot.data.BaseResp;
import com.cds.iot.data.entity.VideoEntity;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/31 17:18
 */
public interface WifiApi {
    public static final String base_url = "http://192.168.43.1:8080/mirror/";

    @GET("videoInfo/{isLock}/{imei}")
    Observable<BaseResp<List<VideoEntity>>> getVideoInfo(@Path("isLock") int isLock,
                                                         @Path("imei") String imei,
                                                         @Query("offset") int offset,
                                                         @Query("limit") int limit);

    @GET("imageInfo/{imei}")
    Observable<BaseResp<List<VideoEntity>>> getImageInfo(@Path("imei") String imei,
                                                         @Query("offset") int offset,
                                                         @Query("limit") int limit);

    @Streaming
    @GET
    Observable<ResponseBody> downloadFileWithDynamicUrlAsync(@Url String fileUrl);
}
