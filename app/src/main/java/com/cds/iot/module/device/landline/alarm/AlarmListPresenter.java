package com.cds.iot.module.device.landline.alarm;

import android.text.TextUtils;

import com.cds.iot.App;
import com.cds.iot.data.BaseResp;
import com.cds.iot.data.entity.AlarmInfoResp;
import com.cds.iot.data.entity.AlarmReq;
import com.cds.iot.data.entity.SaveAlarmReq;
import com.cds.iot.data.entity.TelphoneInfoReq;
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
 * @Author: ${auther}
 * @CreateDate: 2018/11/19 14:07
 * @Version: 3.0.0
 */
public class AlarmListPresenter implements AlarmListContract.Presenter{
    public final static String TAG = "AlarmListPresenter";
    private AlarmListContract.View view;
    private HttpApi mHttpApi;
    private CompositeDisposable mCompositeDisposable;

    public AlarmListPresenter(AlarmListContract.View view) {
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
    public void getAlarmList(String device_id) {
        TelphoneInfoReq req = new TelphoneInfoReq(device_id);
        mHttpApi.getAlarminfo(new Gson().toJson(req))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp<AlarmInfoResp>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp<AlarmInfoResp> response) {
                        if ("200".equals(response.getInfo().getCode())) {
                            view.getAlarmListSuccess(response.getData());
                        } else {
                            view.getAlarmListFailed();
                            ToastUtils.showShort(App.getInstance(), response.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.getAlarmListFailed();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void updateAlarm(SaveAlarmReq req) {
        Observable.just("")
                .subscribeOn(Schedulers.io())                // 切换至IO线程
                .flatMap(new Function<String, ObservableSource<BaseResp>>() {
                    @Override
                    public ObservableSource<BaseResp> apply(String s) throws Exception {
//                        int userId = Integer.parseInt(PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID,""));
//                        userInfo.setUser_id(userId);
                        String filePath="";
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
                            view.updateAlarmSuccess();
                        } else {
                            view.sendAlarmFailed();
                            ToastUtils.showShort(App.getInstance(), response.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.sendAlarmFailed();
                        super.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void deleteAlarm(String alarm_id) {
        AlarmReq req = new AlarmReq(alarm_id);
        mHttpApi.alarmdelete(new Gson().toJson(req))
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
                            view.deleteAlarmSuccess();
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
    public void sendAlarm(String deviceId) {
        TelphoneInfoReq req = new TelphoneInfoReq(deviceId);
        mHttpApi.alarmsend(new Gson().toJson(req))
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
                            view.sendAlarmSuccess();
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
