package com.cds.iot.module.scenes;


import com.cds.iot.App;
import com.cds.iot.data.BaseResp;
import com.cds.iot.data.entity.GetScenesImgReq;
import com.cds.iot.data.entity.SceneReq;
import com.cds.iot.data.entity.ScenesDelReq;
import com.cds.iot.data.entity.ScenesImg;
import com.cds.iot.data.source.remote.BaseObserver;
import com.cds.iot.data.source.remote.HttpApi;
import com.cds.iot.data.source.remote.HttpFactory;
import com.cds.iot.util.ToastUtils;
import com.google.gson.Gson;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ScenesEditPresenter implements ScenesEditContract.Presenter {
    public final static String TAG = "ScenesEditPresenter";
    private ScenesEditContract.View view;
    private HttpApi mHttpApi;
    private CompositeDisposable mCompositeDisposable;

    public ScenesEditPresenter(ScenesEditContract.View view) {
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
    public void deleteScenes(String user_scene_id) {
        ScenesDelReq req = new ScenesDelReq(user_scene_id);
        mHttpApi.deleteScene(new Gson().toJson(req))
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
                            view.deleteScenesSuccess();
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
    public void updateScenes(SceneReq req) {
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
                            view.updateScenesSuccess();
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
    public void getScenesImg(String scenesId) {
        GetScenesImgReq req = new GetScenesImgReq(scenesId,new ArrayList<String>());
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
                            view.getScenesImgSuccess( response.getData().getScene_img());
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
