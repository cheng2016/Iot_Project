package com.cds.iot.data.source.remote;

import android.content.Context;

import com.cds.iot.data.entity.WXTokenResp;
import com.cds.iot.data.entity.WxUserInfoResp;
import com.cds.iot.util.Logger;
import com.cds.iot.wxapi.WXEntryActivity;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class HttpsImpl {
    public static final String TAG = "HttpsImpl";

    static volatile HttpsImpl sInstance;

    static volatile Https mSSHClient;

    static volatile HttpApi mHttpApi;

    private Context context;

    private HttpsImpl(Context context) {
        this.context = context;
    }

    private Https getSSHClient() {
        if (mSSHClient == null) {
            synchronized (this) {
                mSSHClient = HttpFactory.createWxSSLService(Https.class, context);
            }
        }
        return mSSHClient;
    }

    private HttpApi getHttpApi() {
        if (mHttpApi == null) {
            synchronized (this) {
                mHttpApi = HttpFactory.createRetrofit2(HttpApi.class);
            }
        }
        return mHttpApi;
    }

    //获取唯一单列
    public static HttpsImpl getInstance(Context context) {
        if (sInstance == null) {
            synchronized (HttpsImpl.class) {
                sInstance = new HttpsImpl(context);
            }
        }
        return sInstance;
    }

    public void getWXUserInfo(String code) {
        getSSHClient().getWXToken(WXEntryActivity.WX_APP_ID, WXEntryActivity.WX_APP_SECRET, code, "authorization_code")
                .flatMap(new Function<WXTokenResp, ObservableSource<WxUserInfoResp>>() {
                    @Override
                    public ObservableSource<WxUserInfoResp> apply(WXTokenResp resp) throws Exception {
                        return getSSHClient().getWxUserInfo(resp.getAccess_token(), resp.getOpenid());
                    }
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<WxUserInfoResp>() {
                    @Override
                    public void onError(Throwable e) {
                        Logger.v(TAG, "getWXUserInfo onError  msg：" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(WxUserInfoResp resp) {
                        Logger.v(TAG, "getWXUserInfo onNext  msg：" + new Gson().toJson(resp));
                        EventBus.getDefault().post(resp);
                    }
                });
    }
}
