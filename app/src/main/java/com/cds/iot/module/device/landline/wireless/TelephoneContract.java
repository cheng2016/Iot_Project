package com.cds.iot.module.device.landline.wireless;

import com.cds.iot.data.entity.SaveTelphoneReq;
import com.cds.iot.data.entity.TelphoneInfo;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

/**
 * @author Chengzj
 * @date 2018/10/9 16:49
 */
public interface TelephoneContract {
    interface View extends BaseView<Presenter> {
        void getTelphoneInfoSuccess(TelphoneInfo resp);

        void saveTelphoneInfoSuccess();

        void saveTelphoneInfoFail();
    }

    interface Presenter extends BasePresenter {
        void getTelphoneInfo(String json);

        void saveTelphoneInfo(SaveTelphoneReq req, String path);
    }
}
