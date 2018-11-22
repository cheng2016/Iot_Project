package com.cds.iot.module.review;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AnimUtils;

import chuangyuan.ycj.videolibrary.listener.OnCoverMapImageListener;
import chuangyuan.ycj.videolibrary.listener.VideoInfoListener;
import chuangyuan.ycj.videolibrary.listener.VideoWindowListener;
import chuangyuan.ycj.videolibrary.video.ExoUserPlayer;
import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;
/**
 * Created by Administrator Chengzj
 *
 * @date 2018/11/5 15:09
 */

public class YjPlayerActvity extends BaseActivity {
    private ExoUserPlayer exoPlayerManager;

    private String url;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_yjplayer;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");

        exoPlayerManager = new VideoPlayerManager.Builder(this,VideoPlayerManager.TYPE_PLAY_GESTURE, R.id.exo_play_context_id)
                .setDataSource(new Data2Source(this))
                //设置视频标题
                .setTitle(title)
                //设置水印图
//                .setExoPlayWatermarkImg(R.mipmap.watermark_big)
                //.setPlayUri("/storage/emulated/0/test.ts")
                //.setPlayUri("http://oph6zeldx.bkt.clouddn.com/20130104095750-MzE1ODU1.mp3")
                // .setPlayUri("/storage/emulated/0/DCIM/Camera/VID_20180820_083327.mp4")
                .setPlayUri(url)
                .setPlayerGestureOnTouch(true)
                // .setPlayUri("/storage/sdcard0/DCIM/Camera/VID_20180829_100348.mp4")
                //加载rtmp 协议视频
                //.setPlayUri("rtmp://live.hkstv.hk.lxdns.com/live/hks")
                //加载m3u8
                //.setPlayUri("http://dlhls.cdn.zhanqi.tv/zqlive/35180_KUDhx.m3u8")
                //加载ts.文件
                //.setPlayUri("http://185.73.239.15:25461/live/1/1/924.ts")
                //播放本地视频
                //.setPlayUri("/storage/emulated/0/DCIM/Camera/VID_20170717_011150.mp4")
                //播放列表视频
                // .setPlayUri(listss);
                //设置开始播放进度
                // .setPosition(1000)
                //示例本地路径 或者 /storage/emulated/0/DCIM/Camera/VID_20180215_131926.mp4
                // .setPlayUri(Environment.getExternalStorageDirectory().getAbsolutePath()+"/VID_20170925_154925.mp4")
                //开启线路设置
                // .setShowVideoSwitch(true)
                // .setPlaySwitchUri(0,test,name)
                // .setPlaySwitchUri(0, 0, getString(R.string.uri_test_11), Arrays.asList(test), Arrays.asList(name))
                //设置播放视频倍数  快进播放和慢放播放
                // .setPlaybackParameters(0.5f, 0.5f)
                //是否屏蔽进度控件拖拽快进视频（例如广告视频，（不允许用户））
                //  .setSeekBarSeek(false)
                //设置视循环播放
                // .setLooping(10)
                //视频进度回调
                .addOnWindowListener(new VideoWindowListener() {
                    @Override
                    public void onCurrentIndex(int currentIndex, int windowCount) {
                        Toast.makeText(getApplication(), currentIndex + "windowCount:" + windowCount, Toast.LENGTH_SHORT).show();
                    }
                })
                .addUpdateProgressListener(new AnimUtils.UpdateProgressListener() {
                    @Override
                    public void updateProgress(long position, long bufferedPosition, long duration) {
//                     //   Log.d(TAG,"bufferedPosition:"+position);
                        //    Log.d(TAG,"duration:"+duration);
                    }
                })
                .addOnPreparedListeners(new ExoUserPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(SimpleExoPlayer player) {
                        player.setPlayWhenReady(false);
                    }
                })
                .addVideoInfoListener(new VideoInfoListener() {

                    @Override
                    public void onPlayStart(long currPosition) {

                    }

                    @Override
                    public void onLoadingChanged() {

                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException e) {

                    }

                    @Override
                    public void onPlayEnd() {
                        // Toast.makeText(getApplication(), "asd", Toast.LENGTH_SHORT).show();
                    }


                    @Override
                    public void isPlaying(boolean playWhenReady) {

                    }
                })
                .setOnCoverMapImage(new OnCoverMapImageListener() {
                    @Override
                    public void onCoverMap(ImageView v) {

                    }
                })
                .create();
        exoPlayerManager.setStartOrPause(false);
        exoPlayerManager.startPlayer();
    }

    @Override
    protected void initData() {

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        exoPlayerManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        exoPlayerManager.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        exoPlayerManager.onDestroy();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        exoPlayerManager.onConfigurationChanged(newConfig);//横竖屏切换
    }

    @Override
    public void onBackPressed() {
        if (exoPlayerManager.onBackPressed()) {
            ActivityCompat.finishAfterTransition(this);
            exoPlayerManager.onDestroy();
        }
    }
}
