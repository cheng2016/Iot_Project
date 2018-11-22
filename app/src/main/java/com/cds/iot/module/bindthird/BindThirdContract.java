package com.cds.iot.module.bindthird;

import android.widget.TextView;

import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

public interface BindThirdContract {
    interface View extends BaseView<Presenter> {
        void thirdbindSuccess();

        void getCodeSuccess();
    }

    interface Presenter extends BasePresenter {
        void thirdBind(String phone, String pwd, String code,String openid, String nickname, String headurl);

        void getCode(String phone);

        void countdown(TextView view);
    }
}
