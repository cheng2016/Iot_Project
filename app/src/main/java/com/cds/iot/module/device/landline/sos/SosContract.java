package com.cds.iot.module.device.landline.sos;

import com.cds.iot.data.entity.SaveTelphoneReq;
import com.cds.iot.data.entity.TelphoneInfo;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;
import com.cds.iot.module.device.landline.wireless.TelephoneContract;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/11/6 18:03
 */
public interface SosContract {
    interface View extends BaseView<Presenter> {
        void getTelphoneInfoSuccess(TelphoneInfo resp);

        void saveTelphoneInfoSuccess();

        void saveTelphoneInfoFail();
    }

    interface Presenter extends BasePresenter {
        void getTelphoneInfo(String device_id);

        void saveTelphoneInfo(SaveTelphoneReq req, String path);
    }
}
