package com.cds.iot.module.message;

import com.cds.iot.data.entity.MessageType;
import com.cds.iot.data.entity.SMessage;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

import java.util.List;

public interface MessageContract {
    interface View extends BaseView<Presenter> {
        void queryMessageSuccess(List<SMessage> dataList);

        void queryMessage();

        void getMessageTypeListSuccess(List<MessageType> dataList);
    }

    interface Presenter extends BasePresenter {
        void queryMessage(String typeId,int offset);

        void getMessageTypeList();
    }
}
