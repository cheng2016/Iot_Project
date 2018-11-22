package com.cds.iot.module.forget;

import android.widget.TextView;

import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

public interface ForgetContract {
    interface View extends BaseView<Presenter> {
        void resetPasswordSuccess();

        void getCodeSuccess();
    }

    interface Presenter extends BasePresenter {
        void resetPassword(String phone, String pwd, String code);

        void getCode(String phone);

        void countdown(TextView view);
    }
}
