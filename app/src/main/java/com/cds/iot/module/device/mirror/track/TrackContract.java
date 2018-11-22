package com.cds.iot.module.device.mirror.track;

import com.cds.iot.data.entity.TrackResp;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

/**
 * @author Chengzj
 * @date 2018/10/10 15:10
 */
public interface TrackContract {
    interface View extends BaseView<Presenter> {
        void getTrackSuccess(TrackResp resp);
    }

    interface Presenter extends BasePresenter {
       void getTrack(String deviceId,String dateStr);
    }
}
