package com.cds.iot.module.manage.detail;

import com.cds.iot.data.entity.ManageInfo;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

/**
 * @author Chengzj
 * @date 2018/9/19 17:40
 */
public interface ManageDetailContract {
    interface View extends BaseView<Presenter> {

        void getManageInfoSuccess(ManageInfo resp);

        void getManageInfoFailed();

        void releaseDeviceSuccess();

        void releaseUserSuccess();

        void transferDeviceSuccess();
    }

    interface Presenter extends BasePresenter {
        void getManageInfo(String userId, String deviceId);

        void releaseDevice(String userId,String deviceId);

        void releaseUser(String userId, String deviceId);

        void transferDevice(String userId,String deviceId);
    }
}
