package com.cds.iot.module.feedback;

import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

public interface FeedBackContract {
    interface View extends BaseView<Presenter> {
        void feedbackSuccess();

        void feedbackFailed();
    }

    interface Presenter extends BasePresenter {
        void feedback(String content,String imageUrl);
    }
}
