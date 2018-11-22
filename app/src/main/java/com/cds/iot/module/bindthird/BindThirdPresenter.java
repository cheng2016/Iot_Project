package com.cds.iot.module.bindthird;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.cds.iot.App;
import com.cds.iot.data.BaseResp;
import com.cds.iot.data.Constant;
import com.cds.iot.data.entity.GetCodeReq;
import com.cds.iot.data.entity.ThirdBindReq;
import com.cds.iot.data.source.remote.BaseObserver;
import com.cds.iot.data.source.remote.HttpApi;
import com.cds.iot.data.source.remote.HttpFactory;
import com.cds.iot.util.Logger;
import com.cds.iot.util.ResourceUtils;
import com.cds.iot.util.ToastUtils;
import com.google.gson.Gson;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 绑定第三方
 */
public class BindThirdPresenter implements BindThirdContract.Presenter {
    public final static String TAG = "BindPresenter";
    private BindThirdContract.View view;
    private HttpApi mHttpApi;
    private CompositeDisposable mCompositeDisposable;
    private CountDownTimer mCountDown;

    public BindThirdPresenter(BindThirdContract.View view) {
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
    public void thirdBind(String phone, String pwd, String code,String openid, String nickname, String headurl) {
        ThirdBindReq req = new ThirdBindReq(openid, Constant.WX_PLATFORM_ID,phone, pwd, code,nickname, headurl);
        Logger.i(TAG,"thirdBind：" + new Gson().toJson(req));
        mHttpApi.thirdBind(new Gson().toJson(req), ResourceUtils.getProperties(App.getInstance(), Constant.OS_IS_DEBUG))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp baseResp) {
                        if ("200".equals(baseResp.getInfo().getCode())) {
                            view.thirdbindSuccess();
                        } else {
                            ToastUtils.showShort(App.getInstance(), baseResp.getInfo().getInfo());
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
