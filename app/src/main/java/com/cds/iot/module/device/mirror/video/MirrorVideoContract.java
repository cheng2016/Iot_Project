package com.cds.iot.module.device.mirror.video;

import android.widget.TextView;

import com.cds.iot.data.entity.VideoEntity;
import com.cds.iot.module.BasePresenter;
import com.cds.iot.module.BaseView;

import java.util.List;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/31 17:24
 */
public interface MirrorVideoContract {
    interface View extends BaseView<Presenter> {
        void getVideoSuccess(List<VideoEntity> resp);

        void getVideoFailed();

        void updateProgress(int index, TextView textView, int progress);

        void downloadSuccess(int index, TextView textView, String path);

        void downloadFailed(int index, TextView textView);
    }

    interface Presenter extends BasePresenter {
        void getVideo(int type, int offset, int limit);

        void getImage(int offset, int limit);

        void downloadFileWithDynamicUrlAsync(TextView textView, int index, String url);

        void downloadFile(TextView textView, int index, String url);
    }
}
