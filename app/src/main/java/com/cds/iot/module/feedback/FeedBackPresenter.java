package com.cds.iot.module.feedback;

import android.text.TextUtils;

import com.cds.iot.App;
import com.cds.iot.data.BaseResp;
import com.cds.iot.data.entity.FeedBackReq;
import com.cds.iot.data.source.remote.BaseObserver;
import com.cds.iot.data.source.remote.HttpApi;
import com.cds.iot.data.source.remote.HttpFactory;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.ToastUtils;
import com.google.gson.Gson;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FeedBackPresenter implements FeedBackContract.Presenter {
    public final static String TAG = "FeedBackPresenter";
    private FeedBackContract.View view;
    private HttpApi mHttpApi;
    private CompositeDisposable mCompositeDisposable;

    public FeedBackPresenter(FeedBackContract.View view) {
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
    public void feedback(String content, final String imagePath) {
        int userId = Integer.parseInt(PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID,""));
        String deviceId = PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.DEVICE_ID,"");
        final FeedBackReq req = new FeedBackReq(userId, content, deviceId);
        Observable.just("")
                .subscribeOn(Schedulers.io())                // 切换至IO线程
                .flatMap(new Function<String, ObservableSource<BaseResp>>() {
                    @Override
                    public ObservableSource<BaseResp> apply(String s) throws Exception {
                        File file = new File(imagePath);
                        if(!TextUtils.isEmpty(imagePath) && file.exists()){
                            // 封装请求体
                            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
                            MultipartBody.Part filePart =
                                    MultipartBody.Part.createFormData("imgs", file.getName(), requestFile);

                            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                    .addFormDataPart("content",  new Gson().toJson(req))
                                    .addPart(filePart)
                                    .build();
                            return mHttpApi.feedback(requestBody);
                        }else{
                            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                    .addFormDataPart("content",  new Gson().toJson(req))
                                    .addPart(MultipartBody.Part.createFormData("imgs", ""))
                                    .build();

                            return mHttpApi.feedback(requestBody);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())           // 切换至Android主线程
                .subscribe(new BaseObserver<BaseResp>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp baseResp) {
                        if ("200".equals(baseResp.getInfo().getCode())) {
                            view.feedbackSuccess();
                        } else {
                            view.feedbackFailed();
                            ToastUtils.showShort(App.getInstance(), baseResp.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.feedbackFailed();
                    }
                });
    }
}
