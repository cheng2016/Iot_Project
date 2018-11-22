package com.cds.iot.module.register;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.cds.iot.App;
import com.cds.iot.data.BaseResp;
import com.cds.iot.data.Constant;
import com.cds.iot.data.entity.GetCodeReq;
import com.cds.iot.data.entity.RegisterReq;
import com.cds.iot.data.source.remote.BaseObserver;
import com.cds.iot.data.source.remote.HttpApi;
import com.cds.iot.data.source.remote.HttpFactory;
import com.cds.iot.util.DeviceUtils;
import com.cds.iot.util.MD5Utils;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.ResourceUtils;
import com.cds.iot.util.ToastUtils;
import com.google.gson.Gson;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RegisterPresenter implements RegisterContact.Presenter {
    public final static String TAG = "RegisterPresenter";
    private RegisterContact.View view;
    private HttpApi mHttpApi;
    private CompositeDisposable mCompositeDisposable;

    private CountDownTimer mCountDown;

    public RegisterPresenter(RegisterContact.View view) {
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
        if(mCountDown !=null){
            mCountDown.cancel();
        }
    }

    @Override
    public void register(String phone, String pwd, String code) {
        RegisterReq req = new RegisterReq(phone, MD5Utils.MD5(MD5Utils.MD5(pwd)), code);
        String token = MD5Utils.MD5(phone + DeviceUtils.getDeviceIMEI(App.getInstance()));
        PreferenceUtils.setPrefString(App.getInstance(), PreferenceConstants.ACCESS_TOKEN, token);
        mHttpApi.register(new Gson().toJson(req), ResourceUtils.getProperties(App.getInstance(), Constant.OS_IS_DEBUG))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp baseResp) {
                        if("200".equals(baseResp.getInfo().getCode())){
                            view.registerSuccess();
                        }else{
                            ToastUtils.showShort(App.getInstance(),baseResp.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void getCode(String phone) {
        GetCodeReq req = new GetCodeReq(phone);
        mHttpApi.sendcode(new Gson().toJson(req))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp baseResp) {
                        if("200".equals(baseResp.getInfo().getCode())){
                            view.getCodeSuccess();
                        }else{
                            ToastUtils.showShort(App.getInstance(),baseResp.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void countdown(final TextView view) {
        mCountDown = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                view.setEnabled(false);
                view.setText(millisUntilFinished/1000 + "秒");
            }

            @Override
            public void onFinish() {
                view.setText("获取验证码");
                view.setEnabled(true);
            }
        }.start();
    }
}
