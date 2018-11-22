package com.cds.iot.module.scenes;

import com.cds.iot.data.entity.SceneReq;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

public interface ScenesEditContract {
    interface View extends BaseView<Presenter> {
        void deleteScenesSuccess();

        void updateScenesSuccess();

        void getScenesImgSuccess(String url);
    }

    interface Presenter extends BasePresenter {
        void deleteScenes(String user_scene_id);

        void updateScenes(SceneReq req);

        void getScenesImg(String scenesId);
    }
}
