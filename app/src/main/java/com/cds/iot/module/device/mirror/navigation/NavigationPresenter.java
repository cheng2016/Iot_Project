package com.cds.iot.module.device.mirror.navigation;

import com.cds.iot.App;
import com.cds.iot.data.BaseResp;
import com.cds.iot.data.entity.NavigationReq;
import com.cds.iot.data.source.remote.BaseObserver;
import com.cds.iot.data.source.remote.HttpApi;
import com.cds.iot.data.source.remote.HttpFactory;
import com.cds.iot.util.ToastUtils;
import com.google.gson.Gson;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Chengzj
 * @date 2018/10/10 15:14
 */
public class NavigationPresenter implements NavigationContract.Presenter{
    public final static String TAG = "NavigationPresenter";
    private NavigationContract.View view;
    private HttpApi mHttpApi;
    private CompositeDisposable mCompositeDisposable;

    public NavigationPresenter(NavigationContract.View view) {
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
    public void navigation(String deviceId,String name, String address, Double latitude, Double longtitude) {
        NavigationReq req = new NavigationReq(deviceId,"微信接人",name,address,latitude.toString(),longtitude.toString());
        mHttpApi.navigation(new Gson().toJson(req))
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
                            view.navigationSuccess();
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
