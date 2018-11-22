package com.cds.iot.module.fence.edit;

import com.cds.iot.data.source.remote.HttpApi;
import com.cds.iot.data.source.remote.HttpFactory;

import io.reactivex.disposables.CompositeDisposable;

/**
 * @author Chengzj
 * @date 2018/9/5 16:48
 */
public class FenceEditPresenter implements FenceEditContract.Presenter{
    public final static String TAG = "FenceEditPresenter";
    private FenceEditContract.View view;
    private HttpApi mHttpApi;
    private CompositeDisposable mCompositeDisposable;

    public FenceEditPresenter(FenceEditContract.View view) {
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
