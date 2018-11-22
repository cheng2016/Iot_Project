package com.cds.iot.module.message.admin;


import com.cds.iot.data.entity.ManageInfo;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

/**
 * @author Chengzj
 * @date 2018/9/30 13:59
 */
public interface AdminApplyForContract {
    interface View extends BaseView<Presenter> {
        void getManageInfoSuccess(ManageInfo resp);

        void getManageInfoFailed();

        void processMessageSuccess();
    }

    interface Presenter extends BasePresenter {
        void getManageInfo(String userId, String deviceId);

        void processMessage(String update_user_id, String deviceId,String type);
    }
}
