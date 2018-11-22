package com.cds.iot.module.setting.modifypwd;

import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

public interface ModifyPwdContract {
    interface View extends BaseView<Presenter> {
        void modifyPwdSuccess();
    }

    interface Presenter extends BasePresenter {
        void modifyPwd(String oldPwd, String newPwd);
    }
}
