package com.cds.iot.module.device.mirror.deviceinfo;

import com.cds.iot.App;
import com.cds.iot.data.BaseResp;
import com.cds.iot.data.entity.MirrorInfoReq;
import com.cds.iot.data.entity.MirrorInfoResp;
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

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/11 12:38
 */
public class DeviceInfoPresenter implements DeviceInfoContract.Presenter {
    public final static String TAG = "DeviceInfoPresenter";
    private DeviceInfoContract.View view;
    private HttpApi mHttpApi;
    private CompositeDisposable mCompositeDisposable;

    public DeviceInfoPresenter(DeviceInfoContract.View view) {
        this.view = view;
        view.setPresenter(this);
        mCompositeDisposable = new CompositeDisposable();
        mHttpApi =  HttpFactory.createRetrofit2(HttpApi.class);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }

    @Override
    public void getMirrorInfo(String deviceId) {
        String userId = PreferenceUtils.getPrefString(App.getInstance(),PreferenceConstants.USER_ID,"");
        MirrorInfoReq req = new MirrorInfoReq(deviceId,userId);
        mHttpApi.getMirrorInfo(new Gson().toJson(req))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp<MirrorInfoResp>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp<MirrorInfoResp> response) {
                        if ("200".equals(response.getInfo().getCode())) {
                            view.getMirrorInfoSuccess(response.getData());
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
    public void saveMirrorInfo(String deviceId, String deviceName) {
        String userId = PreferenceUtils.getPrefString(App.getInstance(),PreferenceConstants.USER_ID,"");
        MirrorInfoReq req = new MirrorInfoReq(deviceId,userId,deviceName);
        mHttpApi.saveMirrorInfo(new Gson().toJson(req))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp response) {
                        if ("200".equals(response.getInfo().getCode())) {
                            view.saveMirrorInfoSuccess();
                        } else {
                            ToastUtils.showShort(App.getInstance(), response.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
