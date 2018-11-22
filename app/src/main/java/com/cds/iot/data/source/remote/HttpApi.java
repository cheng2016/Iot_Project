package com.cds.iot.data.source.remote;

import com.cds.iot.data.BaseResp;
import com.cds.iot.data.entity.AlarmInfoResp;
import com.cds.iot.data.entity.CarPosition;
import com.cds.iot.data.entity.Device;
import com.cds.iot.data.entity.DeviceCode;
import com.cds.iot.data.entity.DeviceListResp;
import com.cds.iot.data.entity.FenceInfo;
import com.cds.iot.data.entity.GankDaily;
import com.cds.iot.data.entity.ManageInfo;
import com.cds.iot.data.entity.MessageType;
import com.cds.iot.data.entity.MirrorInfoResp;
import com.cds.iot.data.entity.NewsList;
import com.cds.iot.data.entity.SceneInfo;
import com.cds.iot.data.entity.ScenesImg;
import com.cds.iot.data.entity.TelphoneInfo;
import com.cds.iot.data.entity.TrackResp;
import com.cds.iot.data.entity.UserInfo;
import com.cds.iot.data.entity.UserInfoResp;
import com.cds.iot.data.entity.WxPickupResp;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by chengzj on 2017/6/18.
 */

public interface HttpApi {
    //http://gank.io/api/day/2016/10/12
    public static final String base_url = "http://sit.wecarelove.com/api/app/";

    @GET("day/{year}/{month}/{day}")
    Observable<GankDaily> getDaily(
            @Path("year") int year, @Path("month") int month, @Path("day") int day);

    @GET("list")
    Observable<NewsList> getNewsList(@Query("req_funType") String funType,
                                     @Query("req_count") String count);

    /*---------------------------------------------    用户相关      -----------------------------------------*/

    @POST("user/register")
    Observable<BaseResp> register(@Query("content") String json, @Header("os_is_debug") String is_debug);

    @POST("user/login")
    Observable<BaseResp<UserInfoResp>> login(@Query("content") String json);

    @POST("user/thirdlogin")
    Observable<BaseResp<UserInfoResp>> thirdLogin(@Query("content") String json);

    @POST("user/thirdbind")
    Observable<BaseResp<UserInfoResp>> thirdBind(@Query("content") String json, @Header("os_is_debug") String is_debug);

    @POST("user/thirdpartybind")
    Observable<BaseResp<UserInfoResp>> thirdPartyBind(@Query("content") String json);

    @POST("user/thirdunbind")
    Observable<BaseResp> thirdUnbind(@Query("content") String json);

    @GET("user/info")
    Observable<BaseResp<UserInfo>> getUserInfo(@Query("content") String json);

    @POST("user/info")
    Observable<BaseResp> updateUserInfo(@Body RequestBody Body);

    @POST("user/sendcode")
    Observable<BaseResp> sendcode(@Query("content") String json);

    @POST("user/resetpwd")
    Observable<BaseResp> resetpwd(@Query("content") String json, @Header("os_is_debug") String is_debug);

    @POST("user/updatepwd")
    Observable<BaseResp> updatepwd(@Query("content") String json);

    @POST("user/phonenumber")
    Observable<BaseResp> updatephonenumber(@Query("content") String json, @Header("os_is_debug") String is_debug);

    @POST("user/feedback")
    Observable<BaseResp> feedback(@Body RequestBody Body);

    /*---------------------------------------------    设备相关      -----------------------------------------*/

    @POST("device/delete")
    Observable<BaseResp> deleteDevice(@Query("content") String json);

    @GET("device/info")
    Observable<BaseResp<List<Device>>> getDeviceList(@Query("content") String json);

    @POST("device/info")
    Observable<BaseResp> addDevice(@Query("content") String json);

    @GET("device/validatedevice")
    Observable<BaseResp> validateDevice(@Query("content") String json);

    @GET("device/messagetypelist")
    Observable<BaseResp<List<MessageType>>>  getMessageTypeList(@Query("content") String json);

    //获取设备编码
    @GET("devicemanager/devicecode")
    Observable<BaseResp<DeviceCode>> devicecode(@Query("content") String json);


    /*---------------------------------------------    场景相关      -----------------------------------------*/

    @POST("scene/delete")
    Observable<BaseResp> deleteScene(@Query("content") String json);

    @GET("scene/info")
    Observable<BaseResp<SceneInfo>> getSceneInfo(@Query("content") String json);

    @POST("scene/info")
    Observable<BaseResp> insertOrUpdateScene(@Query("content") String json);

    @GET("scene/sceneimgs")
    Observable<BaseResp<ScenesImg>> getSceneImgs(@Query("content") String json);

    /*---------------------------------------------    版本相关      -----------------------------------------*/

    @GET("version/update")
    Observable<BaseResp> updateVersion(@Query("content") String json);


    /*---------------------------------------------    电子围栏相关      -----------------------------------------*/
    @GET("fence/info")
    Observable<BaseResp<List<FenceInfo>>> getFenceInfo(@Query("content") String json);

    @POST("fence/info")
    Observable<BaseResp> updateFenceInfo(@Query("content") String json);

    @POST("fence/delete")
    Observable<BaseResp> deleteFenceInfo(@Query("content") String json);

    /*---------------------------------------------    绑定管理 设备申请      -----------------------------------------*/
    @GET("devicemanager/info")
    Observable<BaseResp<ManageInfo>> getManageInfo(@Query("content") String json);

    @POST("devicemanager/info")
    Observable<BaseResp> updateManageInfo(@Query("content") String json);

    //4.9.2、更改管理员
    @POST("devicemanager/updatemanager")
    Observable<BaseResp> updatemanager(@Query("content") String json);

    @GET("device/info")
    Observable<BaseResp<List<DeviceListResp>>> getDeviceList2(@Query("content") String json);

    /*---------------------------------------------    无线座机      -----------------------------------------*/
    @GET("telphone/info")
    Observable<BaseResp<TelphoneInfo>> getTelphoneInfo(@Query("content") String json);

    @POST("telphone/info")
    Observable<BaseResp> saveTelphoneInfo(@Body RequestBody Body);

    //删除闹钟
    @POST("telphone/alarmdelete")
    Observable<BaseResp> alarmdelete(@Query("content") String json);

    //查询座机闹钟
    @GET("telphone/alarminfo")
    Observable<BaseResp<AlarmInfoResp>> getAlarminfo(@Query("content") String json);

    //添加、修改座机闹钟
    @POST("telphone/alarminfo")
    Observable<BaseResp> saveAlarminfo(@Body RequestBody Body);

    //发送闹钟
    @POST("telphone/alarmsend")
    Observable<BaseResp> alarmsend(@Query("content") String json);

    /*---------------------------------------------    后视镜      -----------------------------------------*/

    //获取车辆位置
    @GET("devicemanager/position")
    Observable<BaseResp<CarPosition>> getPosition(@Query("content") String json);

    //获取轨迹
    @GET("devicemanager/track")
    Observable<BaseResp<TrackResp>> getTrack(@Query("content") String json);

    //远程拍照
    @POST("devicemanager/photograph")
    Observable<BaseResp> getRemotePhotograph(@Query("content") String json);

    //远程录像
    @POST("devicemanager/videotape")
    Observable<BaseResp> getRemoteVideotape(@Query("content") String json);

    //获取后视镜信息
    @GET("backmirror/info")
    Observable<BaseResp<MirrorInfoResp>> getMirrorInfo(@Query("content") String json);

    //修改后视镜信息
    @POST("backmirror/info")
    Observable<BaseResp> saveMirrorInfo(@Query("content") String json);

    //预约导航
    @POST("backmirror/navigation")
    Observable<BaseResp> navigation(@Query("content") String json);

    //微信接人
    @GET("backmirror/wxpickup")
    Observable<BaseResp<WxPickupResp>> wxPickup(@Query("content") String json);
}
