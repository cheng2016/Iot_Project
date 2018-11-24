package com.cds.iot.module.manage.detail;

import com.cds.iot.App;
import com.cds.iot.data.BaseResp;
import com.cds.iot.data.Constant;
import com.cds.iot.data.entity.ManageDeviceReq;
import com.cds.iot.data.entity.ManageInfo;
import com.cds.iot.data.entity.TransferDeviceReq;
import com.cds.iot.data.source.remote.BaseObserver;
import com.cds.iot.data.source.remote.HttpApi;
import com.cds.iot.data.source.remote.HttpFactory;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.ToastUtils;
import com.google.gson.Gson;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Chengzj
 * @date 2018/9/19 17:41
 */
public class ManageDetailPresenter implements ManageDetailContract.Presenter{
    public final static String TAG = "ManageDetailPresenter";
    private ManageDetailContract.View view;
    private HttpApi mHttpApi;
    private CompositeDisposable mCompositeDisposable;

    public ManageDetailPresenter(ManageDetailContract.View view) {
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
    public void getManageInfo(String userId, String deviceId) {
        ManageDeviceReq req = new ManageDeviceReq(userId,deviceId);
        mHttpApi.getManageInfo(new Gson().toJson(req))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp<ManageInfo>>(){

                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp<ManageInfo> response) {
                        if ("200".equals(response.getInfo().getCode())) {
                            view.getManageInfoSuccess(response.getData());
                        } else {
                            view.getManageInfoFailed();
                            ToastUtils.showShort(App.getInstance(), response.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.getManageInfoFailed();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void releaseDevice(String update_user_id, String deviceId) {
        String[] ids = new String[]{update_user_id};
        String userId = PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID,"");
        ManageDeviceReq req = new ManageDeviceReq(userId,ids,deviceId, Constant.MANAGE_RELEASE);
        mHttpApi.updateManageInfo(new Gson().toJson(req))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp>(){

                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp response) {
                        if ("200".equals(response.getInfo().getCode())) {
                            view.releaseDeviceSuccess();
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
    public void releaseUser(String update_user_id, String deviceId) {
        String[] ids = new String[]{update_user_id};
        String userId = PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID,"");
        ManageDeviceReq req = new ManageDeviceReq(userId,ids,deviceId, Constant.MANAGE_RELEASE);
        mHttpApi.updateManageInfo(new Gson().toJson(req))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp>(){

                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp response) {
                        if ("200".equals(response.getInfo().getCode())) {
                            view.releaseUserSuccess();
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
    public void transferDevice(String userId, String deviceId) {
        TransferDeviceReq req = new TransferDeviceReq(deviceId,userId);
        mHttpApi.updatemanager(new Gson().toJson(req))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp>(){

                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp response) {
                        if ("200".equals(response.getInfo().getCode())) {
                            view.transferDeviceSuccess();
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
