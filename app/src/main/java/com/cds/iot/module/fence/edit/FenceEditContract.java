package com.cds.iot.module.fence.edit;

import com.cds.iot.data.entity.FenceInfo;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

/**
 * @author Chengzj
 * @date 2018/9/5 16:48
 */
public interface FenceEditContract {
    interface View extends BaseView<Presenter> {
        void saveFenceInfoSuccess();
    }

    interface Presenter extends BasePresenter {
        void saveFenceInfo(FenceInfo req);
    }
}
