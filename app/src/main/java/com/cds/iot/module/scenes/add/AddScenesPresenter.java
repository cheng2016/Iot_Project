package com.cds.iot.module.scenes.add;

import com.cds.iot.App;
import com.cds.iot.data.BaseResp;
import com.cds.iot.data.entity.GetScenesImgReq;
import com.cds.iot.data.entity.SceneInfo;
import com.cds.iot.data.entity.SceneInfoReq;
import com.cds.iot.data.entity.SceneReq;
import com.cds.iot.data.entity.ScenesImg;
import com.cds.iot.data.source.remote.BaseObserver;
import com.cds.iot.data.source.remote.HttpApi;
import com.cds.iot.data.source.remote.HttpFactory;
import com.cds.iot.util.ToastUtils;
import com.google.gson.Gson;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddScenesPresenter implements AddScenesContract.Presenter {
    public final static String TAG = "ScenesEditPresenter";
    private AddScenesContract.View view;
    private HttpApi mHttpApi;
    private CompositeDisposable mCompositeDisposable;

    public AddScenesPresenter(AddScenesContract.View view) {
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
    public void getSceneInfo(String userId, String typeId) {
        SceneInfoReq req = new SceneInfoReq(userId,typeId);
        mHttpApi.getSceneInfo(new Gson().toJson(req))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp<SceneInfo>>(){
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp<SceneInfo> response) {
                        if("200".equals(response.getInfo().getCode())){
                            view.getSceneInfoSuccess(response.getData());
                        }else{
                            ToastUtils.showShort(App.getInstance(),response.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void addSceneInfo(SceneReq req) {
        mHttpApi.insertOrUpdateScene(new Gson().toJson(req))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp>(){
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp response) {
                        if("200".equals(response.getInfo().getCode())){
                            view.addSceneInfoSuccess();
                        }else{
                            view.addSceneInfoFailed();
                            ToastUtils.showShort(App.getInstance(),response.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.addSceneInfoFailed();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void getScenesImg(String scenesId,List<String> ids) {
        GetScenesImgReq req = new GetScenesImgReq(scenesId,ids);
        mHttpApi.getSceneImgs(new Gson().toJson(req))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp<ScenesImg>>(){
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp<ScenesImg> response) {
                        if("200".equals(response.getInfo().getCode())){
                            view.getScenesImgSuccess(response.getData().getScene_img());
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
