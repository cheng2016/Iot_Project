package com.cds.iot.module.user;

import com.cds.iot.App;
import com.cds.iot.data.BaseResp;
import com.cds.iot.data.entity.UserInfo;
import com.cds.iot.data.entity.UserInfoResp;
import com.cds.iot.data.source.remote.BaseObserver;
import com.cds.iot.data.source.remote.HttpApi;
import com.cds.iot.data.source.remote.HttpFactory;
import com.cds.iot.util.ToastUtils;
import com.google.gson.Gson;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UserPresenter implements UserContract.Presenter {
    public final static String TAG = "UserPresenter";
    private UserContract.View view;
    private HttpApi mHttpApi;
    private CompositeDisposable mCompositeDisposable;

    public UserPresenter(UserContract.View view) {
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
                        if("200".equals(response.getInfo().getCode())){
                            view.getUserInfoSuccess(response.getData());
                        }else{
                            ToastUtils.showShort(App.getInstance(),response.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
