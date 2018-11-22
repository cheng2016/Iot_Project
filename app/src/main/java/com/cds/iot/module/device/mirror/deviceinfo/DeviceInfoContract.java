package com.cds.iot.module.device.mirror.deviceinfo;

import com.cds.iot.data.entity.MirrorInfoResp;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/11 12:37
 */
public interface DeviceInfoContract {
    interface View extends BaseView<Presenter> {
        void getMirrorInfoSuccess(MirrorInfoResp resp);

        void saveMirrorInfoSuccess();
    }

    interface Presenter extends BasePresenter {
        void getMirrorInfo(String deviceId);

        void saveMirrorInfo(String deviceId,String deviceName);
    }
}
