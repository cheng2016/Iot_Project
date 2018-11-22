package com.cds.iot.module.setting.modifypwd;

import com.cds.iot.App;
import com.cds.iot.data.BaseResp;
import com.cds.iot.data.entity.ModifyPwdReq;
import com.cds.iot.data.source.remote.BaseObserver;
import com.cds.iot.data.source.remote.HttpApi;
import com.cds.iot.data.source.remote.HttpFactory;
import com.cds.iot.util.MD5Utils;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.ToastUtils;
import com.google.gson.Gson;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ModifyPwdPresenter implements ModifyPwdContract.Presenter{
    public final static String TAG = "MessageNotifyPresenter";
    private ModifyPwdContract.View view;
    private HttpApi mHttpApi;
    private CompositeDisposable mCompositeDisposable;

    public ModifyPwdPresenter(ModifyPwdContract.View view) {
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
    public void modifyPwd(String oldPwd, String newPwd) {
        oldPwd = MD5Utils.MD5(MD5Utils.MD5(oldPwd));
        newPwd = MD5Utils.MD5(MD5Utils.MD5(newPwd));
        ModifyPwdReq req = new ModifyPwdReq(oldPwd,newPwd, Integer.parseInt(PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID,"")));
        mHttpApi.updatepwd(new Gson().toJson(req))
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
                            view.modifyPwdSuccess();
                        }else{
                            ToastUtils.showShort(App.getInstance(),baseResp.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
