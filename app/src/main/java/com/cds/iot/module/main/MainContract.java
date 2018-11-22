package com.cds.iot.module.main;

import com.cds.iot.data.entity.NewsList;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

/**
 * Created by chengzj on 2017/6/17.
 */

public interface MainContract {
    interface View extends BaseView<Presenter>{
        void showNewList(NewsList newsList);
    }

    interface Presenter extends BasePresenter{

    }
}
