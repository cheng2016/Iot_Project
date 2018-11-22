package com.cds.iot.module.review;

import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.cds.iot.App;
import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.util.Logger;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.mp4.Mp4Extractor;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
//import com.google.android.exoplayer2.source.dash.DashChunkSource;
//import com.google.android.exoplayer2.source.dash.DashMediaSource;
//import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
//import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/15 17:51
 */
public class ExoPlayActivity extends BaseActivity implements Player.EventListener {
    private PowerManager.WakeLock wakeLock;

    PlayerView playerView;

    SimpleExoPlayer player;

    // 创建带宽
    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

    // 创建轨道选择工厂
    TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

    // 创建轨道选择器实例
    TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

    private String url;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        playerView = findViewById(R.id.player_view);

        LoadControl loadControl = new JCBufferingLoadControl();

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        /**常亮*/
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "WakeLock");
        }
        wakeLock.acquire();
    }

    @Override
    protected void initData() {
        url = getIntent().getStringExtra("url");
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)));

        // 传入Uri、加载数据的工厂、解析数据的工厂，就能创建出MediaSource
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url));

        //使用dash的解析库
//        DefaultDashChunkSource.Factory localFactory = new DefaultDashChunkSource.Factory(dataSourceFactory);
//        videoSource = new DashMediaSource.Factory(localFactory, dataSourceFactory).createMediaSource(Uri.parse(url));

        //使用hls解析库
//        HlsMediaSource hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url));


        // Prepare the player with the source.
        player.prepare(videoSource);
        // Add a listener to receive events from the player.
        player.addListener(this);
        startPlayer();
    }

    private void startPlayer() {
        playerView.setPlayer(player);
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wakeLock != null) {
            wakeLock.acquire();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放播放器
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }


    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object o, int i) {
        Logger.i(TAG, "onTimelineChanged");
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
        Logger.i(TAG, "onTracksChanged");
    }

    @Override
    public void onLoadingChanged(boolean b) {
        Logger.i(TAG, "onLoadingChanged");
    }

    @Override
    public void onPlayerStateChanged(boolean b, int i) {
        Logger.i(TAG, "onPlayerStateChanged");
    }

    @Override
    public void onRepeatModeChanged(int i) {
        Logger.i(TAG, "onRepeatModeChanged");
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean b) {
        Logger.i(TAG, "onShuffleModeEnabledChanged");
    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {
        Logger.i(TAG, "onPlayerError");
    }

    @Override
    public void onPositionDiscontinuity(int i) {
        Logger.i(TAG, "onPositionDiscontinuity");
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        Logger.i(TAG, "onPlaybackParametersChanged");
    }

    @Override
    public void onSeekProcessed() {
        Logger.i(TAG, "onSeekProcessed");
    }
}
