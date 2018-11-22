package com.cds.iot.module.device.mirror.navigation;

import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

/**
 * @author Chengzj
 * @date 2018/10/10 15:14
 */
public interface NavigationContract {
    interface View extends BaseView<Presenter> {
        void navigationSuccess();
    }

    interface Presenter extends BasePresenter {
        void navigation(String deviceId, String name, String address, Double latitude, Double longtitude);
    }
}
