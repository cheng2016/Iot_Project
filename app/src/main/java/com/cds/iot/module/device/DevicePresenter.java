package com.cds.iot.module.device;

import android.content.Context;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.cds.iot.App;
import com.cds.iot.data.BaseResp;
import com.cds.iot.data.Constant;
import com.cds.iot.data.entity.AddDeviceReq;
import com.cds.iot.data.entity.DeviceListResp;
import com.cds.iot.data.entity.DeviceReq;
import com.cds.iot.data.entity.ManageDeviceReq;
import com.cds.iot.data.entity.SceneInfo;
import com.cds.iot.data.entity.SceneInfoReq;
import com.cds.iot.data.entity.WetherInfo;
import com.cds.iot.data.source.remote.BaseObserver;
import com.cds.iot.data.source.remote.HttpApi;
import com.cds.iot.data.source.remote.HttpFactory;
import com.cds.iot.data.source.remote.WHttps;
import com.cds.iot.module.location.AMapLocationStrategy;
import com.cds.iot.module.location.LocationStrategy;
import com.cds.iot.module.location.UpdateLocationListener;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.ToastUtils;
import com.google.gson.Gson;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DevicePresenter implements DeviceContract.Presenter, UpdateLocationListener {
    public final static String TAG = "DevicePresenter";

    public final static String KEY = "2ffd42fd0b96b71643f35d095247c3f5";

    private DeviceContract.View view;
    private HttpApi mHttpApi;
    private CompositeDisposable mCompositeDisposable;

    LocationStrategy locationStrategy;

    public DevicePresenter(DeviceContract.View view) {
        this.view = view;
        view.setPresenter(this);
        mCompositeDisposable = new CompositeDisposable();
        mHttpApi = HttpFactory.createRetrofit2(HttpApi.class);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
        if (locationStrategy != null) {
            locationStrategy.destoryLocation();
        }
    }

    @Override
    public void getLocation(Context context) {
        locationStrategy = new AMapLocationStrategy(context);
        locationStrategy.setListener(this);
        locationStrategy.requestLocation();
    }

    @Override
    public void getWetherInfo(Context context, String city) {
        WHttps https = HttpFactory.createWetherSSLService(WHttps.class, context);
        https.getWeatherInfo(KEY, city)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<WetherInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(WetherInfo wetherInfo) {
                        view.getWetherInfoSuccess(wetherInfo);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void getSceneInfo(String userId, String typeId) {
        SceneInfoReq req = new SceneInfoReq(userId, typeId);
        mHttpApi.getSceneInfo(new Gson().toJson(req))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp<SceneInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp<SceneInfo> response) {
                        if ("200".equals(response.getInfo().getCode())) {
                            view.getSceneInfoSuccess(response.getData());
                        } else {
                            ToastUtils.showShort(App.getInstance(), response.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void getDeviceList(String userId) {
        DeviceReq req = new DeviceReq(userId);
        mHttpApi.getDeviceList2(new Gson().toJson(req))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp<List<DeviceListResp>>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp<List<DeviceListResp>> baseResp) {
                        if ("200".equals(baseResp.getInfo().getCode())) {
                            view.getDeviceListSuccess(baseResp.getData());
                        } else {
                            view.getDeviceListFailed();
                            ToastUtils.showShort(App.getInstance(), baseResp.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.getDeviceListFailed();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void reapplyDevice(String deviceCode, String deviceName) {
        String userId = PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID, "");
        AddDeviceReq req = new AddDeviceReq(deviceCode, deviceName, Integer.valueOf(userId));
        mHttpApi.addDevice(new Gson().toJson(req))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp baseResp) {
                        if ("200".equals(baseResp.getInfo().getCode())) {
                            view.reapplyDeviceSuccess();
                        } else {
                            ToastUtils.showShort(App.getInstance(), baseResp.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void releaseDevice(String userId, String deviceId) {
        String[] ids = new String[]{userId};
        ManageDeviceReq req = new ManageDeviceReq(userId,ids,deviceId, Constant.MANAGE_RELEASE);
        mHttpApi.updateManageInfo(new Gson().toJson(req))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp>(){

                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp response) {
                        if ("200".equals(response.getInfo().getCode())) {
                            view.releaseDeviceSuccess();
                        } else {
                            ToastUtils.showShort(App.getInstance(), response.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void updateLocationChanged(AMapLocation location, int gpsCount) {
        String city = location.getCity();
        if (!TextUtils.isEmpty(city)) {
            locationStrategy.stopLocation();
            view.getLocationSuccess(city);
        }
    }
}
