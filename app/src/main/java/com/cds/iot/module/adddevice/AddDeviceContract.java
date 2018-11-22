package com.cds.iot.module.adddevice;

import com.cds.iot.data.entity.DeviceCode;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

public interface AddDeviceContract {
    interface View extends BaseView<Presenter> {
        void validateDeviceSuccess();

        void showRequestDialog(String title,String info);

        void addDeviceSuccess();

        void submitRequestSuccess();

        void deviceCodeSuccess(DeviceCode code);
    }

    interface Presenter extends BasePresenter {
        void validateDevice(String device_code,String device_type_id);

        void addDevice(String deviceId, String deviceName);

        void submitRequest(String deviceId, String deviceName);

        void deviceCode(String qr_url);
    }
}
