package com.cds.iot.module.device.mirror;

import android.content.Context;

import com.cds.iot.data.entity.CarPosition;
import com.cds.iot.data.entity.WxPickupResp;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

/**
 * @author Chengzj
 * @date 2018/10/10 10:27
 */
public interface RearviewMirrorContract {
    interface View extends BaseView<Presenter> {
        void getPositionSuccess(CarPosition resp);

        void getRemotePhotographSuccess();

        void getRemotePhotographFail();

        void getRemoteVideotapeSuccess();

        void getRemoteVideotapeFail();

        void wxPickupSuccess(WxPickupResp resp);
    }

    interface Presenter extends BasePresenter {
        void getPosition(String deviceId);

        void getRemotePhotograph(String deviceId);

        void getRemoteVideotape(String deviceId);

        void wxPickup(String deviceId);

        void shareWebPage(Context context, WxPickupResp resp);

        void shareWebPageNetwork(Context context, WxPickupResp resp);
    }
}
