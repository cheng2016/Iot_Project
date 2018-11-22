package com.cds.iot.module.manage;

import com.cds.iot.data.entity.DeviceListResp;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

import java.util.List;

/**
 * @author Chengzj
 * @date 2018/9/19 17:40
 */
public interface ManageBindContract {
    interface View extends BaseView<Presenter> {
        void getDeviceListSuccess(List<DeviceListResp> resp);

        void getDeviceListFailed();
    }

    interface Presenter extends BasePresenter {
        void getDeviceList(String userId);
    }
}
