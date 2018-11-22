package com.cds.iot.module.user;

import com.cds.iot.data.entity.UserInfo;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

public interface UserContract {
    interface View extends BaseView<Presenter> {
        void getUserInfoSuccess(UserInfo resp);
    }

    interface Presenter extends BasePresenter {
        void getUserInfo(int userId);
    }
}
