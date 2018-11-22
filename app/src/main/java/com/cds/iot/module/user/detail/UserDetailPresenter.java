package com.cds.iot.module.user.detail;

import android.content.Context;
import android.text.TextUtils;

import com.cds.iot.App;
import com.cds.iot.data.BaseResp;
import com.cds.iot.data.entity.ThirdBindReq;
import com.cds.iot.data.entity.ThirdUnbindReq;
import com.cds.iot.data.entity.UserInfo;
import com.cds.iot.data.entity.UserInfoResp;
import com.cds.iot.data.entity.WXTokenResp;
import com.cds.iot.data.entity.WxUserInfoResp;
import com.cds.iot.data.source.remote.BaseObserver;
import com.cds.iot.data.source.remote.HttpApi;
import com.cds.iot.data.source.remote.HttpFactory;
import com.cds.iot.data.source.remote.Https;
import com.cds.iot.util.Logger;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.ToastUtils;
import com.cds.iot.wxapi.WXEntryActivity;
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

public class UserDetailPresenter implements UserDetailContract.Presenter {
    public final static String TAG = "UserDetailPresenter";
    private UserDetailContract.View view;
    private HttpApi mHttpApi;
    private Https mHttps;
    private CompositeDisposable mCompositeDisposable;

    public UserDetailPresenter(UserDetailContract.View view) {
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
    public void getUserInfo(int userId) {
        UserInfoResp req = new UserInfoResp(userId);
        mHttpApi.getUserInfo(new Gson().toJson(req))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp<UserInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp<UserInfo> response) {
                        if ("200".equals(response.getInfo().getCode())) {
                            view.getUserInfoSuccess(response.getData());
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
    public void getWXUserInfo(Context context, String code) {
        mHttps = HttpFactory.createWxSSLService(Https.class, context);
        mHttps.getWXToken(WXEntryActivity.WX_APP_ID, WXEntryActivity.WX_APP_SECRET, code, "authorization_code")
                .flatMap(new Function<WXTokenResp, ObservableSource<WxUserInfoResp>>() {
                    @Override
                    public ObservableSource<WxUserInfoResp> apply(WXTokenResp resp) throws Exception {
                        Logger.v(TAG, "getWXToken is work  resp：" + new Gson().toJson(resp));
                        return mHttps.getWxUserInfo(resp.getAccess_token(), resp.getOpenid());
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<WxUserInfoResp>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.getWXUserInfoFailed();
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(WxUserInfoResp resp) {
                        Logger.v(TAG, "getWXUserInfo onNext  msg：" + new Gson().toJson(resp));
                        view.getWXUserInfoSuccess(resp);
                    }
                });
    }

    @Override
    public void updateUserInfo(final UserInfo userInfo) {
        Observable.just("")
                .subscribeOn(Schedulers.io())                // 切换至IO线程
                .flatMap(new Function<String, ObservableSource<BaseResp>>() {
                    @Override
                    public ObservableSource<BaseResp> apply(String s) throws Exception {
                        int userId = Integer.parseInt(PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID,""));
                        userInfo.setUser_id(userId);
                        File file = new File(userInfo.getHead_img());

                        if (!TextUtils.isEmpty(userInfo.getHead_img()) && file.exists()) {

                            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
                            MultipartBody.Part filePart  = MultipartBody.Part.createFormData("head_img", file.getName(), requestFile);

                            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                    .addFormDataPart("content",  new Gson().toJson(userInfo))
                                    .addPart(filePart)
                                    .build();

                            return mHttpApi.updateUserInfo(requestBody);
                        } else {
                            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                    .addFormDataPart("content",  new Gson().toJson(userInfo))
                                    .addPart(MultipartBody.Part.createFormData("head_img", ""))
                                    .build();

                            return mHttpApi.updateUserInfo(requestBody);
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
                            view.updateUserInfoSuccess();
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
    public void thirdPartyBind(String platform_id, int login_type, String userId) {
        ThirdBindReq req = new ThirdBindReq(userId ,platform_id,login_type);
        mHttpApi.thirdPartyBind(new Gson().toJson(req))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp response) {
                        if ("200".equals(response.getInfo().getCode())) {
                            view.thirdPartyBindSuccess();
                        } else {
                            view.thirdPartyBindFailed();
                            ToastUtils.showShort(App.getInstance(), response.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void thirdUnbind(String userId, int login_type) {
        ThirdUnbindReq req = new ThirdUnbindReq(userId,login_type);
        mHttpApi.thirdUnbind(new Gson().toJson(req))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp response) {
                        if ("200".equals(response.getInfo().getCode())) {
                            view.thirdUnbindSuccess();
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
