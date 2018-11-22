package com.cds.iot.module.user.changephone;

import android.widget.TextView;

import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

public interface ChangePhoneContract {
    interface View extends BaseView<Presenter> {
        void getCodeSuccess();

        void changePhoneSuccess();
    }

    interface Presenter extends BasePresenter {
        void getCode(String phone);

        void countdown(TextView view);

        void changePhone(String newphone,String code);
    }
}
