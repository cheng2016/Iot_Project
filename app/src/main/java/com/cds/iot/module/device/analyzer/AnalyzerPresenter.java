package com.cds.iot.module.device.analyzer;

import com.cds.iot.data.source.remote.HttpApi;
import com.cds.iot.data.source.remote.HttpFactory;

import io.reactivex.disposables.CompositeDisposable;

/**
 * @author Chengzj
 * @date 2018/10/10 11:25
 */
public class AnalyzerPresenter implements AnalyzerContract.Presenter{
    public final static String TAG = "AnalyzerPresenter";
    private AnalyzerContract.View view;
    private HttpApi mHttpApi;
    private CompositeDisposable mCompositeDisposable;

    public AnalyzerPresenter(AnalyzerContract.View view) {
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
