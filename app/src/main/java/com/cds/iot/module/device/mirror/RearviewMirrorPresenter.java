package com.cds.iot.module.device.mirror;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cds.iot.App;
import com.cds.iot.R;
import com.cds.iot.data.BaseResp;
import com.cds.iot.data.entity.CarPosition;
import com.cds.iot.data.entity.DeviceManageReq;
import com.cds.iot.data.entity.WxPickupResp;
import com.cds.iot.data.source.remote.BaseObserver;
import com.cds.iot.data.source.remote.HttpApi;
import com.cds.iot.data.source.remote.HttpFactory;
import com.cds.iot.util.Logger;
import com.cds.iot.util.ToastUtils;
import com.cds.iot.util.Utils;
import com.cds.iot.wxapi.WXEntryActivity;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Chengzj
 * @date 2018/10/10 10:27
 */
public class RearviewMirrorPresenter implements RearviewMirrorContract.Presenter {
    public final static String TAG = "RearviewMirrorPresenter";
    private RearviewMirrorContract.View view;
    private HttpApi mHttpApi;
    private CompositeDisposable mCompositeDisposable;

    public RearviewMirrorPresenter(RearviewMirrorContract.View view) {
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
    public void getPosition(String deviceId) {
        DeviceManageReq req = new DeviceManageReq(deviceId);
        mHttpApi.getPosition(new Gson().toJson(req))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp<CarPosition>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp<CarPosition> response) {
                        if ("200".equals(response.getInfo().getCode())) {
                            view.getPositionSuccess(response.getData());
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
    public void getRemotePhotograph(String deviceId) {
        DeviceManageReq req = new DeviceManageReq(deviceId);
        mHttpApi.getRemotePhotograph(new Gson().toJson(req))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp response) {
                        if ("200".equals(response.getInfo().getCode())) {
                            view.getRemotePhotographSuccess();
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
    public void getRemoteVideotape(String deviceId) {
        DeviceManageReq req = new DeviceManageReq(deviceId);
        mHttpApi.getRemoteVideotape(new Gson().toJson(req))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp response) {
                        if ("200".equals(response.getInfo().getCode())) {
                            view.getRemoteVideotapeSuccess();
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
    public void wxPickup(String deviceId) {
        DeviceManageReq req = new DeviceManageReq(deviceId);
        mHttpApi.wxPickup(new Gson().toJson(req))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp<WxPickupResp>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp<WxPickupResp> response) {
                        if ("200".equals(response.getInfo().getCode())) {
                            view.wxPickupSuccess(response.getData());
                        } else {
                            ToastUtils.showShort(App.getInstance(), response.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private static final int THUMB_SIZE = 150;

    /**
     * 微信分享链接
     *
     * @param context
     * @param resp
     */
    public void shareWebPage(final Context context, final WxPickupResp resp) {
        Logger.i(TAG,"shareWebPage");
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = resp.getUrl();

        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = resp.getTitle();
        msg.description = resp.getContent();
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.send_wxpickup_thumb);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = Utils.bmpToByteArray(thumbBmp, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;//发送到聊天界面——WXSceneSession，发送到朋友圈——WXSceneTimeline,添加到微信收藏——WXSceneFavorite
        IWXAPI api = WXAPIFactory.createWXAPI(context, WXEntryActivity.WX_APP_ID, false);
        if (!api.isWXAppInstalled()) {
            //提醒用户没有按照微信
            ToastUtils.showShort(context, "请先安装微信客户端");
            return;
        }
        api.sendReq(req);
    }

    /**
     * 微信分享链接
     *
     * @param context
     * @param resp
     */
    public void shareWebPageNetwork(final Context context, final WxPickupResp resp) {
        Logger.i(TAG,"shareWebPageNetwork");
        Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
                Bitmap bitmap = Picasso.with(context).load(resp.getImg_url()).resize(THUMB_SIZE,THUMB_SIZE).get();
                emitter.onNext(bitmap);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap thumb) throws Exception {
                        WXWebpageObject webpage = new WXWebpageObject();
                        webpage.webpageUrl = resp.getUrl();

                        WXMediaMessage msg = new WXMediaMessage(webpage);
                        msg.title = resp.getTitle();
                        msg.description = resp.getContent();
                        msg.thumbData = Utils.bmpToByteArray(thumb, true);
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = buildTransaction("webpage");
                        req.message = msg;
                        req.scene = SendMessageToWX.Req.WXSceneSession;
                        IWXAPI api = WXAPIFactory.createWXAPI(context, WXEntryActivity.WX_APP_ID, false);
                        if (!api.isWXAppInstalled()) {
                            //提醒用户没有按照微信
                            ToastUtils.showShort(context, "请先安装微信客户端");
                            return;
                        }
                        api.sendReq(req);
                    }
                });
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

}
