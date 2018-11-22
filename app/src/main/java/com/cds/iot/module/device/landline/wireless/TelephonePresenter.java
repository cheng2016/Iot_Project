package com.cds.iot.module.device.landline.wireless;

import android.text.TextUtils;

import com.cds.iot.App;
import com.cds.iot.data.BaseResp;
import com.cds.iot.data.entity.SaveTelphoneReq;
import com.cds.iot.data.entity.TelphoneInfo;
import com.cds.iot.data.entity.TelphoneInfoReq;
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

/**
 * @author Chengzj
 * @date 2018/10/9 16:50
 */
public class TelephonePresenter implements TelephoneContract.Presenter{
    public final static String TAG = "TelephonePresenter";
    private TelephoneContract.View view;
    private HttpApi mHttpApi;
    private CompositeDisposable mCompositeDisposable;

    public TelephonePresenter(TelephoneContract.View view) {
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
    public void getTelphoneInfo(String device_id) {
        String userId = PreferenceUtils.getPrefString(App.getInstance(),PreferenceConstants.USER_ID,"");
        TelphoneInfoReq req = new TelphoneInfoReq(device_id,userId);
        mHttpApi.getTelphoneInfo(new Gson().toJson(req))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp<TelphoneInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp<TelphoneInfo> response) {
                        if ("200".equals(response.getInfo().getCode())) {
                            view.getTelphoneInfoSuccess(response.getData());
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
    public void saveTelphoneInfo(final SaveTelphoneReq req, final String path) {
        Observable.just("")
                .subscribeOn(Schedulers.io())                // 切换至IO线程
                .flatMap(new Function<String, ObservableSource<BaseResp>>() {
                    @Override
                    public ObservableSource<BaseResp> apply(String s) throws Exception {
//                        int userId = Integer.parseInt(PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID,""));
//                        userInfo.setUser_id(userId);
                        File file = new File(path);

                        if (!TextUtils.isEmpty(path) && file.exists()) {

                            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
                            MultipartBody.Part filePart  = MultipartBody.Part.createFormData("record_file", file.getName(), requestFile);

                            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                    .addFormDataPart("content",  new Gson().toJson(req))
                                    .addPart(filePart)
                                    .build();

                            return mHttpApi.saveTelphoneInfo(requestBody);
                        } else {
                            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                    .addFormDataPart("content",  new Gson().toJson(req))
                                    .addPart(MultipartBody.Part.createFormData("record_file", ""))
                                    .build();

                            return mHttpApi.saveTelphoneInfo(requestBody);
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
                            view.saveTelphoneInfoSuccess();
                        } else {
                            view.saveTelphoneInfoFail();
                            ToastUtils.showShort(App.getInstance(), response.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
