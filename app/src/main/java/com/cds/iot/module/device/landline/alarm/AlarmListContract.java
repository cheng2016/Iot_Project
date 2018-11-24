package com.cds.iot.module.device.landline.alarm;

import com.cds.iot.data.entity.AlarmInfoResp;
import com.cds.iot.data.entity.SaveAlarmReq;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

/**
 * @Author: ${auther}
 * @CreateDate: 2018/11/19 14:07
 * @Version: 3.0.0
 */
public interface AlarmListContract {
    interface View extends BaseView<Presenter> {
        void getAlarmListSuccess(AlarmInfoResp resp);

        void getAlarmListFailed();

        void updateAlarmSuccess();

        void deleteAlarmSuccess();

        void sendAlarmSuccess();

        void sendAlarmFailed();
    }

    interface Presenter extends BasePresenter {
        void getAlarmList(String device_id);

        void updateAlarm(SaveAlarmReq info);

        void deleteAlarm(String alarm_id);

        void sendAlarm(String deviceId);
    }
}
