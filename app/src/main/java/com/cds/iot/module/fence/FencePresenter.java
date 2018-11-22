package com.cds.iot.module.fence;

import com.cds.iot.App;
import com.cds.iot.data.BaseResp;
import com.cds.iot.data.entity.FenceInfo;
import com.cds.iot.data.entity.FenceInfoReq;
import com.cds.iot.data.source.remote.BaseObserver;
import com.cds.iot.data.source.remote.HttpApi;
import com.cds.iot.data.source.remote.HttpFactory;
import com.cds.iot.util.ToastUtils;
import com.google.gson.Gson;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author chengzj
 * @date 2018/9/5 13:52
 */
public class FencePresenter implements FenceContract.Presenter {
    public final static String TAG = "FencePresenter";
    private FenceContract.View view;
    private HttpApi mHttpApi;
    private CompositeDisposable mCompositeDisposable;

    public FencePresenter(FenceContract.View view) {
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
    public void getFenceInfo(String deviceId) {
        FenceInfoReq req = new FenceInfoReq(deviceId);
        mHttpApi.getFenceInfo(new Gson().toJson(req))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp<List<FenceInfo>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp<List<FenceInfo>> baseResp) {
                        if ("200".equals(baseResp.getInfo().getCode())) {
                            view.getFenceInfoSuccess(baseResp.getData());
                        } else {
                            view.getFenceInfoFail();
                            ToastUtils.showShort(App.getInstance(), baseResp.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void updateFenceInfo(FenceInfo info) {
        mHttpApi.updateFenceInfo(new Gson().toJson(info))
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
                            view.updateFenceInfoSuccess();
                        } else {
                            ToastUtils.showShort(App.getInstance(), baseResp.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
