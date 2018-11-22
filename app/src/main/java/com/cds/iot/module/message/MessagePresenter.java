package com.cds.iot.module.message;

import android.text.TextUtils;

import com.cds.iot.App;
import com.cds.iot.data.BaseResp;
import com.cds.iot.data.entity.MessageType;
import com.cds.iot.data.entity.SMessage;
import com.cds.iot.data.entity.UserReq;
import com.cds.iot.data.source.local.SMessageDaoUtils;
import com.cds.iot.data.source.local.greendao.SMessageDao;
import com.cds.iot.data.source.remote.BaseObserver;
import com.cds.iot.data.source.remote.HttpApi;
import com.cds.iot.data.source.remote.HttpFactory;
import com.cds.iot.util.Logger;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.ToastUtils;
import com.google.gson.Gson;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MessagePresenter implements MessageContract.Presenter {
    public final static String TAG = "MessagePresenter";
    private MessageContract.View view;
    private HttpApi mHttpApi;
    private CompositeDisposable mCompositeDisposable;
    private SMessageDaoUtils daoUtils;
    private String userId;

    public MessagePresenter(MessageContract.View view) {
        this.view = view;
        view.setPresenter(this);
        mCompositeDisposable = new CompositeDisposable();
        mHttpApi = HttpFactory.createRetrofit2(HttpApi.class);
        daoUtils = new SMessageDaoUtils(App.getInstance());
        userId = PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID, "");
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }

    @Override
    public void queryMessage(String typeId, int offset) {
        Logger.i(TAG, "queryMessage typeId：" + typeId + "  offset：" + offset);
        List<SMessage> messageList;
        if (TextUtils.isEmpty(typeId)) {
            messageList = daoUtils.querySMessage("USER_ID = ?", new String[]{userId}, SMessageDao.Properties.Id, offset, MessageFragment.REQUEST_NUM);//条件查询
        } else {
            messageList = daoUtils.querySMessage("USER_ID = ? AND DEVICE_TYPE = ?", new String[]{userId, typeId}, SMessageDao.Properties.Tailtime, offset, MessageFragment.REQUEST_NUM);//条件查询
        }
        Logger.i(TAG, "queryMessage messageList：" + new Gson().toJson(messageList));
        view.queryMessageSuccess(messageList);
    }

    @Override
    public void getMessageTypeList() {
        mHttpApi.getMessageTypeList(new Gson().toJson(new UserReq(userId)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp<List<MessageType>>>(){

                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp<List<MessageType>> resp) {
                        if("200".equals(resp.getInfo().getCode())){
                            view.getMessageTypeListSuccess(resp.getData());
                        }else{
                            ToastUtils.showShort(App.getInstance(),resp.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
