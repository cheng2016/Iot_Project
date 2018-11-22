package com.cds.iot.module.device.landline.alarm.defail;

import android.text.TextUtils;

import com.cds.iot.App;
import com.cds.iot.data.BaseResp;
import com.cds.iot.data.entity.SaveAlarmReq;
import com.cds.iot.data.source.remote.BaseObserver;
import com.cds.iot.data.source.remote.HttpApi;
import com.cds.iot.data.source.remote.HttpFactory;
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

/**
 * @Author: chengzj
 * @CreateDate: 2018/11/19 13:48
 * @Version: 3.0.0
 */
public class AlarmPresenter implements AlarmContract.Presenter{
    public final static String TAG = "AlarmPresenter";
    private AlarmContract.View view;
    private HttpApi mHttpApi;
    private CompositeDisposable mCompositeDisposable;

    public AlarmPresenter(AlarmContract.View view) {
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
    public void saveAlarmInfo(SaveAlarmReq req, String filePath) {
        Observable.just("")
                .subscribeOn(Schedulers.io())                // 切换至IO线程
                .flatMap(new Function<String, ObservableSource<BaseResp>>() {
                    @Override
                    public ObservableSource<BaseResp> apply(String s) throws Exception {
//                        int userId = Integer.parseInt(PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID,""));
//                        userInfo.setUser_id(userId);
                        File file = new File(filePath);

                        if (!TextUtils.isEmpty(filePath) && file.exists()) {

                            RequestBody requestFile = RequestBody.create(MediaType.parse("audio/wav"), file);
                            MultipartBody.Part filePart = MultipartBody.Part.createFormData("record_file", file.getName(), requestFile);

                            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                    .addFormDataPart("content", new Gson().toJson(req))
                                    .addPart(filePart)
                                    .build();

                            return mHttpApi.saveAlarminfo(requestBody);
                        } else {
                            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                    .addFormDataPart("content", new Gson().toJson(req))
                                    .addPart(MultipartBody.Part.createFormData("record_file", ""))
                                    .build();

                            return mHttpApi.saveAlarminfo(requestBody);
                        }
                    }
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp response) {
                        if ("200".equals(response.getInfo().getCode())) {
                            view.saveAlarmInfoSuccess();
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
