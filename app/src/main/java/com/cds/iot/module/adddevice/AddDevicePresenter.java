package com.cds.iot.module.adddevice;

import com.cds.iot.App;
import com.cds.iot.data.BaseResp;
import com.cds.iot.data.entity.AddDeviceReq;
import com.cds.iot.data.entity.DeviceCode;
import com.cds.iot.data.entity.DeviceCodeReq;
import com.cds.iot.data.entity.ValidatedeviceReq;
import com.cds.iot.data.source.remote.BaseObserver;
import com.cds.iot.data.source.remote.HttpApi;
import com.cds.iot.data.source.remote.HttpFactory;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.ToastUtils;
import com.google.gson.Gson;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddDevicePresenter implements AddDeviceContract.Presenter {
    public final static String TAG = "AddDevicePresenter";
    private AddDeviceContract.View view;
    private HttpApi mHttpApi;
    private CompositeDisposable mCompositeDisposable;

    public AddDevicePresenter(AddDeviceContract.View view) {
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
    }

    @Override
    public void validateDevice(String device_code, String device_type_id) {
        String userId = PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID, "");
        ValidatedeviceReq req = new ValidatedeviceReq(userId, device_code, device_type_id);
        mHttpApi.validateDevice(new Gson().toJson(req))
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
                            view.validateDeviceSuccess();
                        } else if ("5007".equals(baseResp.getInfo().getCode())) {
                            view.showRequestDialog(baseResp.getInfo().getTitle(), baseResp.getInfo().getInfo());
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
    public void addDevice(String deviceId, String deviceName) {
        String userId = PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID, "");
        AddDeviceReq req = new AddDeviceReq(deviceId, deviceName, Integer.valueOf(userId));
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
                            view.addDeviceSuccess();
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
    public void submitRequest(String deviceId, String deviceName) {
        String userId = PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID, "");
        AddDeviceReq req = new AddDeviceReq(deviceId, deviceName, Integer.valueOf(userId));
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
                            view.submitRequestSuccess();
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
    public void deviceCode(String qr_url) {
        DeviceCodeReq req = new DeviceCodeReq(qr_url);
        mHttpApi.devicecode(new Gson().toJson(req))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp<DeviceCode>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp<DeviceCode> resp) {
                        if ("200".equals(resp.getInfo().getCode())) {
                            view.deviceCodeSuccess(resp.getData());
                        } else {
                            ToastUtils.showShort(App.getInstance(), resp.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
