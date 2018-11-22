package com.cds.iot.module.scenes.adddevice;

import com.cds.iot.data.entity.Device;
import com.cds.iot.data.entity.DeviceCode;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

import java.util.List;

public interface AddScenesDeviceContract {
    interface View extends BaseView<Presenter> {
        void showRequestDialog(String title,String info);

        void getDeviceSuccess(List<Device> deviceList);

        void validateDeviceSuccess();

        void submitRequestSuccess();

        void deviceCodeSuccess(DeviceCode code);
    }

    interface Presenter extends BasePresenter {
        void submitRequest(String deviceId, String deviceName);

        void getDeviceList(String userId, String deviceType);

        void validateDevice(String device_code,String device_type_id);

        void deviceCode(String qr_url);
    }
}
