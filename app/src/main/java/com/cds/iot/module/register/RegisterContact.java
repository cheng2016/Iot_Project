package com.cds.iot.module.register;

import android.widget.TextView;

import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

public interface RegisterContact {
    interface View extends BaseView<Presenter> {
        void registerSuccess();

        void getCodeSuccess();
    }

    interface Presenter extends BasePresenter {
        void register(String phone, String password, String code);

        void getCode(String phone);

        void countdown(TextView view);
    }
}
