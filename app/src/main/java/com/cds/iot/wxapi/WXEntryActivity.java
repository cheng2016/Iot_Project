/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */

package com.cds.iot.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.util.Logger;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 微信客户端回调activity示例
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    public static final String TAG = "WXEntryActivity";
    // IWXAPI 是第三方app和微信通信的openapi接口
    private static IWXAPI api;

    public final static String WX_APP_ID = "wxcbd8a3449f397b6d";
    public final static String WX_APP_SECRET = "6205c76bd854e300786ad50d198b3e88";

    private String transaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView currrentTextView = new TextView(this);
        setContentView(currrentTextView);
        api = WXAPIFactory.createWXAPI(this, WX_APP_ID, false);
        api.handleIntent(getIntent(), this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(transaction)) {
            transaction.startsWith("webpage");
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(Object event) {

    }

    @Override
    public void onReq(BaseReq req) {
        Logger.v("WXEntryActivity onReq", "is work  resp：" + new Gson().toJson(req));
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                break;
            default:
                break;
        }
    }

    @Override
    public void onResp(BaseResp resp) {
        transaction = resp.transaction;
        Logger.v("WXEntryActivity onResp", "is work  resp：" + new Gson().toJson(resp));
        int result = 0;
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK://获取成功
                result = R.string.errcode_success;
                SendAuth.Resp resp1 = (SendAuth.Resp) resp;//这里做一下转型就是
                EventBus.getDefault().post(resp1);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.errcode_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.errcode_deny;
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                result = R.string.errcode_unsupported;
                break;
            default:
                result = R.string.errcode_unknown;
                break;
        }
        this.finish();
    }
}
