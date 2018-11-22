package com.cds.iot.module.user.detail;

import android.content.Context;

import com.cds.iot.data.entity.UserInfo;
import com.cds.iot.data.entity.WxUserInfoResp;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

public interface UserDetailContract {
    interface View extends BaseView<Presenter> {
        void getUserInfoSuccess(UserInfo resp);

        void updateUserInfoSuccess();

        void getWXUserInfoSuccess(WxUserInfoResp resp);

        void getWXUserInfoFailed();

        void thirdUnbindSuccess();

        void thirdPartyBindSuccess();

        void thirdPartyBindFailed();
    }

    interface Presenter extends BasePresenter {
        void getUserInfo(int userId);

        void getWXUserInfo(Context context, String code);

        void updateUserInfo(UserInfo userInfo);

        void thirdPartyBind(String platform_id,int login_type,String userId);

        void thirdUnbind(String userId,int login_type);
    }
}
