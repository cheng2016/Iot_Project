package com.cds.iot.module.review;
/*

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.util.MediaUtils;
import com.dou361.ijkplayer.listener.OnShowThumbnailListener;
import com.dou361.ijkplayer.widget.PlayStateParams;
import com.dou361.ijkplayer.widget.PlayerView;


*//**
 * ========================================
 * <p/>
 * 版 权：深圳市晶网科技控股有限公司 版权所有 （C） 2015
 * <p/>
 * 作 者：陈冠明
 * <p/>
 * 个人网站：http://www.dou361.com
 * <p/>
 * 版 本：1.0
 * <p/>
 * 创建日期：2015/11/18 9:40
 * <p/>
 * 描 述：直播全屏竖屏场景
 * <p/>
 * <p/>
 * 修订历史：
 * <p/>
 * ========================================
 *//*
public class PlayerLiveActivity extends BaseActivity {

    private PlayerView player;
    private Context mContext;
    private View rootView;
    private String url = "http://hdl.9158.com/live/744961b29380de63b4ff129ca6b95849.flv";
    private String title = "标题";
    private PowerManager.WakeLock wakeLock;

    @Override
    protected int getLayoutId() {
        return R.layout.simple_player_view_player;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mContext = this;
        rootView = getWindow().getDecorView();
        *//**常亮*//*
        PowerManager powerManager = (PowerManager)getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "WakeLock");
        }
        wakeLock.acquire();

        player = new PlayerView(this, rootView)
                .setTitle(title)
                .setScaleType(PlayStateParams.fitparent)
                .hideMenu(true)
                .hideSteam(true)
                .setForbidDoulbeUp(true)
                .hideCenterPlayer(true)
                .hideControlPanl(true)
                .showThumbnail(new OnShowThumbnailListener() {
                    @Override
                    public void onShowThumbnail(ImageView ivThumbnail) {
//                        Glide.with(mContext)
//                                .load("http://pic2.nipic.com/20090413/406638_125424003_2.jpg")
//                                .placeholder(R.color.cl_default)
//                                .error(R.color.cl_error)
//                                .into(ivThumbnail);
                    }
                });


        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        player.setPlaySource(url).startPlay();

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();
        }
        MediaUtils.muteAudioFocus(mContext, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
        MediaUtils.muteAudioFocus(mContext, false);
        if (wakeLock != null) {
            wakeLock.acquire();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (player != null) {
            player.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        if (player != null && player.onBackPressed()) {
            return;
        }
        super.onBackPressed();
        if (wakeLock != null) {
            wakeLock.release();
        }
    }
}*/
