package com.cds.iot.module.login;

import android.content.Context;

import com.cds.iot.App;
import com.cds.iot.data.BaseResp;
import com.cds.iot.data.Constant;
import com.cds.iot.data.entity.LoginReq;
import com.cds.iot.data.entity.ThridLoginReq;
import com.cds.iot.data.entity.UserInfoResp;
import com.cds.iot.data.entity.WXTokenResp;
import com.cds.iot.data.entity.WxUserInfoResp;
import com.cds.iot.data.source.remote.BaseObserver;
import com.cds.iot.data.source.remote.HttpApi;
import com.cds.iot.data.source.remote.HttpFactory;
import com.cds.iot.data.source.remote.Https;
import com.cds.iot.util.DeviceUtils;
import com.cds.iot.util.Logger;
import com.cds.iot.util.MD5Utils;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.ToastUtils;
import com.cds.iot.wxapi.WXEntryActivity;
import com.google.gson.Gson;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class LoginPresenter implements LoginContract.Presenter {
    public final static String TAG = "LoginPresenter";
    private LoginContract.View view;
    private HttpApi mHttpApi;
    private CompositeDisposable mCompositeDisposable;

    private Https mHttps;

    public LoginPresenter(LoginContract.View view) {
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
    public void login(final String name, final String pwd) {
        LoginReq req = new LoginReq(name, MD5Utils.MD5(MD5Utils.MD5(pwd)));
        String token = MD5Utils.MD5(name + DeviceUtils.getDeviceIMEI(App.getInstance()));
        PreferenceUtils.setPrefString(App.getInstance(), PreferenceConstants.ACCESS_TOKEN, token);
        mHttpApi.login(new Gson().toJson(req))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp<UserInfoResp>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp<UserInfoResp> resp) {
                        if ("200".equals(resp.getInfo().getCode())) {
                            //存入系统变量中
                            PreferenceUtils.setPrefString(App.getInstance(), PreferenceConstants.USER_NAME, name);
                            PreferenceUtils.setPrefString(App.getInstance(), PreferenceConstants.USER_PASSWORD, MD5Utils.MD5(MD5Utils.MD5(pwd)));
                            PreferenceUtils.setPrefString(App.getInstance(), PreferenceConstants.USER_ID, resp.getData().getUser_id() + "");
                            view.loginSuccess();
                        } else {
                            view.loginFailed();
                            ToastUtils.showShort(App.getInstance(), resp.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.loginFailed();
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
    public void thirdLogin(final WxUserInfoResp wxUserInfoResp) {
        ThridLoginReq req = new ThridLoginReq(wxUserInfoResp.getUnionid(), Constant.WX_PLATFORM_ID);
        Logger.i(TAG,"thridLogin req：" + new Gson().toJson(req));
        mHttpApi.thirdLogin(new Gson().toJson(req))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp<UserInfoResp>>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp<UserInfoResp> resp) {
                        if ("200".equals(resp.getInfo().getCode())) {
                            PreferenceUtils.setPrefString(App.getInstance(), PreferenceConstants.USER_ID,  resp.getData().getUser_id()+ "");
                            view.thirdLoginSuccess();
                        } else {
                            if("5006".equals(resp.getInfo().getCode())){
                                view.thirdbind(wxUserInfoResp);
                            }else{
                                view.thirdLoginFailed();
                                ToastUtils.showShort(App.getInstance(), resp.getInfo().getInfo());
                            }
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
