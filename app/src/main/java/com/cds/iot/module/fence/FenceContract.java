package com.cds.iot.module.fence;

import com.cds.iot.data.entity.FenceInfo;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

import java.util.List;

/**
 * @author chengzj
 * @date 2018/9/5 13:51
 */
public interface FenceContract {
    interface View extends BaseView<Presenter> {
        void getFenceListSuccess(List<FenceInfo> resp);

        void getFenceListFail();

        void updateFenceInfoSuccess();

        void deleteFenceInfoSuccess();
    }

    interface Presenter extends BasePresenter {
        void getFenceList(String deviceId);

        void updateFenceInfo(FenceInfo info);

        void deleteFenceInfo(String id);
    }
}
