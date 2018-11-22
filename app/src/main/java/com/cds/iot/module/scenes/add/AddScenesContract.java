package com.cds.iot.module.scenes.add;

import com.cds.iot.data.entity.SceneInfo;
import com.cds.iot.data.entity.SceneReq;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

import java.util.List;

public interface AddScenesContract {
    interface View extends BaseView<Presenter> {
        void getSceneInfoSuccess(SceneInfo senceInfo);

        void addSceneInfoSuccess();

        void addSceneInfoFailed();

        void getScenesImgSuccess(String url);
    }

    interface Presenter extends BasePresenter {
        void getSceneInfo(String userId, String typeId);

        void addSceneInfo(SceneReq req);

        void getScenesImg(String scenesId,List<String> ids);
    }
}
