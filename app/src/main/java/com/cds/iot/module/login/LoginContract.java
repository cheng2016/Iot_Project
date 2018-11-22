package com.cds.iot.module.login;

import android.content.Context;

import com.cds.iot.data.entity.WxUserInfoResp;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

public interface LoginContract {
    interface View extends BaseView<Presenter> {
        void loginSuccess();

        void loginFailed();

        void getWXUserInfoSuccess(WxUserInfoResp resp);

        void getWXUserInfoFailed();

        void thirdLoginSuccess();

        void thirdLoginFailed();

        void thirdbind(WxUserInfoResp resp);
    }

    interface Presenter extends BasePresenter {
        void login(String name,String pwd);

        void getWXUserInfo(Context context, String code);

        void thirdLogin(WxUserInfoResp resp);
    }
}
