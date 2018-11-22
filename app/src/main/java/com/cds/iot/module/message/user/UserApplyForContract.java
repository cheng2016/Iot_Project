package com.cds.iot.module.message.user;

import com.cds.iot.data.entity.ManageInfo;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

/**
 * @author Chengzj
 * @date 2018/9/30 13:59
 */
public interface UserApplyForContract {
    interface View extends BaseView<Presenter> {
        void getManageInfoSuccess(ManageInfo resp);

        void getManageInfoFailed();

        void revokeApplySuccess();
    }

    interface Presenter extends BasePresenter {
        void getManageInfo(String userId, String deviceId);

        void revokeApply(String userId, String deviceId);
    }
}
