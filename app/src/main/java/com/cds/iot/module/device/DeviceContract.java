package com.cds.iot.module.device;

import android.content.Context;

import com.cds.iot.data.entity.DeviceListResp;
import com.cds.iot.data.entity.SceneInfo;
import com.cds.iot.data.entity.WetherInfo;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

import java.util.List;

public interface DeviceContract {
    interface View extends BaseView<Presenter> {
        void getWetherInfoSuccess(WetherInfo wetherInfo);

        void getLocationSuccess(String city);

        void getSceneInfoSuccess(SceneInfo deviceInfo);

        void getDeviceListSuccess(List<DeviceListResp> resp);

        void getDeviceListFailed();

        void releaseDeviceSuccess();

        void reapplyDeviceSuccess();
    }

    interface Presenter extends BasePresenter {
        void getLocation(Context context);

        void getWetherInfo(Context context, String city);

        void getSceneInfo(String userId, String typeId);

        void getDeviceList(String userId);

        void reapplyDevice(String deviceCode, String deviceName);

        void releaseDevice(String userId,String deviceId);
    }
}
