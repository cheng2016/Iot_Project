package com.cds.iot.module.device.landline.alarm.defail;

import com.cds.iot.data.entity.SaveAlarmReq;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

/**
 * @Author: chengzj
 * @CreateDate: 2018/11/19 13:48
 * @Version: 3.0.0
 */
public interface AlarmContract {
    interface View extends BaseView<Presenter> {
        void saveAlarmInfoSuccess();

        void saveAlarmInfoFailed();
    }

    interface Presenter extends BasePresenter {
        void saveAlarmInfo(SaveAlarmReq req, String filePath);
    }
}
