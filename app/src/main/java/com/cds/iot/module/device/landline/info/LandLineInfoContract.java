package com.cds.iot.module.device.landline.info;

import com.cds.iot.data.entity.MirrorInfoResp;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

/**
 * @Author: ${USER_NAME}
 * @CreateDate: 2018/11/19 15:57
 * @Version: 3.0.0
 */
public interface LandLineInfoContract {
    interface View extends BaseView<Presenter> {
        void getDeviceInfoSuccess(MirrorInfoResp resp);

        void getDeviceInfoFailed();

        void saveDeviceInfoSuccess();
    }

    interface Presenter extends BasePresenter {
        void getDeviceInfo(String deviceId);

        void saveDeviceInfo(String deviceId,String deviceName);
    }
}
