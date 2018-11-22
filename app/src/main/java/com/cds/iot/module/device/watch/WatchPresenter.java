package com.cds.iot.module.device.watch;

import com.cds.iot.data.source.remote.HttpApi;
import com.cds.iot.data.source.remote.HttpFactory;

import io.reactivex.disposables.CompositeDisposable;

/**
 * @author Chengzj
 * @date 2018/10/10 11:28
 */
public class WatchPresenter implements WatchContract.Presenter{
    public final static String TAG = "WatchPresenter";
    private WatchContract.View view;
    private HttpApi mHttpApi;
    private CompositeDisposable mCompositeDisposable;

    public WatchPresenter(WatchContract.View view) {
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
}
