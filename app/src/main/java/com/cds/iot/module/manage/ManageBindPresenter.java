package com.cds.iot.module.manage;

import android.os.Looper;

import com.cds.iot.App;
import com.cds.iot.data.BaseResp;
import com.cds.iot.data.entity.DeviceListResp;
import com.cds.iot.data.entity.DeviceReq;
import com.cds.iot.data.source.remote.BaseObserver;
import com.cds.iot.data.source.remote.HttpApi;
import com.cds.iot.data.source.remote.HttpFactory;
import com.cds.iot.util.ToastUtils;
import com.google.gson.Gson;

import java.util.List;
import java.util.logging.Handler;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Chengzj
 * @date 2018/9/19 17:40
 */
public class ManageBindPresenter implements ManageBindContract.Presenter {
    public final static String TAG = "ManageBindPresenter";
    private ManageBindContract.View view;
    private HttpApi mHttpApi;
    private CompositeDisposable mCompositeDisposable;

    public ManageBindPresenter(ManageBindContract.View view) {
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
                        new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                view.getDeviceListFailed();
                            }
                        });
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
