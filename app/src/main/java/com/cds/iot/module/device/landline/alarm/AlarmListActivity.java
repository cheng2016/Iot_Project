package com.cds.iot.module.device.landline.alarm;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.data.Constant;
import com.cds.iot.data.entity.AlarmInfo;
import com.cds.iot.data.entity.AlarmInfoResp;
import com.cds.iot.data.entity.SaveAlarmReq;
import com.cds.iot.module.adapter.AlarmAdapter;
import com.cds.iot.module.device.landline.alarm.defail.AlarmActivity;
import com.cds.iot.util.Logger;
import com.cds.iot.util.ToastUtils;
import com.cds.iot.view.MyAlertDialog;
import com.cheng.refresh.library.PullToRefreshBase;
import com.cheng.refresh.library.PullToRefreshListView;

import java.io.IOException;

import butterknife.Bind;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/11/13 18:18
 */
public class AlarmListActivity extends BaseActivity implements View.OnClickListener, AlarmListContract.View, AlarmAdapter.onAlarmChangeListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener<ListView> {
    @Bind(R.id.right_img)
    AppCompatImageView rightImg;
    @Bind(R.id.empty_layout)
    LinearLayout emptyLayout;
    @Bind(R.id.content_layout)
    View contentLayout;
    @Bind(R.id.refresh_listView)
    PullToRefreshListView refreshListView;
    @Bind(R.id.synchronization_btn)
    RelativeLayout synchronizationBtn;
    @Bind(R.id.synchronization_tv)
    AppCompatTextView synchronizationTv;

    private ListView mListView;

    private String deviceId;

    private boolean isAdmin;

    AlarmAdapter adapter;

    AlarmListContract.Presenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_alarm_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((TextView) findViewById(R.id.title)).setText("远程定时提醒");
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.right_button).setVisibility(View.VISIBLE);
        findViewById(R.id.right_button).setOnClickListener(this);
        rightImg.setVisibility(View.VISIBLE);
        rightImg.setImageResource(R.mipmap.btn_addfence);
        synchronizationBtn.setOnClickListener(this);
        refreshListView.setPullLoadEnabled(false);//上拉加载是否可用
        refreshListView.setScrollLoadEnabled(false);//判断滑动到底部加载是否可用
        refreshListView.setPullRefreshEnabled(true);//设置是否能下拉
        refreshListView.setOnRefreshListener(this);
        mListView = refreshListView.getRefreshableView();
    }

    @Override
    protected void initData() {
        new AlarmListPresenter(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            adapter = new AlarmAdapter(this);
            adapter.setListener(this);
            mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(this);
            mPresenter.getAlarmList(deviceId);
        }
        mLoadingView.showLoading();
        mLoadingView.setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getAlarmList(deviceId);
            }
        });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        Logger.i(TAG,"onPullDownToRefresh");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPresenter.getAlarmList(deviceId);
            }
        }, 1600);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
        Logger.i(TAG,"onPullUpToRefresh");
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.right_button:
                Intent intent = new Intent();
                intent.putExtra("deviceId", deviceId);
                Bundle bundle = new Bundle();
                bundle.putSerializable("alarmInfo",null);
                intent.putExtras(bundle);
                intent.setClass(this, AlarmActivity.class);
                startActivityForResult(intent, RESULT_FIRST_USER);
                break;
            case R.id.synchronization_btn:
                showProgressDilog();
                mPresenter.sendAlarm(deviceId);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(isAdmin){
            Intent intent = new Intent();
            intent.putExtra("deviceId", deviceId);
            intent.putExtra("isAdmin", isAdmin);
            Bundle bundle = new Bundle();
            bundle.putSerializable("alarmInfo", adapter.getDataList().get(position));
            intent.putExtras(bundle);
            intent.setClass(this, AlarmActivity.class);
            startActivityForResult(intent, RESULT_FIRST_USER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mPresenter.getAlarmList(deviceId);
        }
    }

    @Override
    public void getAlarmListSuccess(AlarmInfoResp resp) {
        mLoadingView.showContent();
        isAdmin = Constant.ALARM_ADMIN.equals(resp.getIs_admin()) ? true : false;
        if (isAdmin) {
            findViewById(R.id.right_button).setVisibility(View.VISIBLE);
            synchronizationBtn.setClickable(true);
            synchronizationBtn.setBackground(getResources().getDrawable(R.drawable.button_float_selector));
            synchronizationTv.setText("同步至座机");
            synchronizationTv.setTextColor(getResources().getColor(R.color.white));
            synchronizationTv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.icn_synctotel), null, null, null);
        } else {
            findViewById(R.id.right_button).setVisibility(View.INVISIBLE);
            synchronizationBtn.setClickable(false);
            synchronizationBtn.setBackgroundColor(getResources().getColor(R.color.white));
            synchronizationTv.setText("仅限管理员设置");
            synchronizationTv.setTextColor(getResources().getColor(R.color.enable_color));
            synchronizationTv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        if (resp.getAlramInfo() != null && resp.getAlramInfo().size() > 0) {
            adapter.setDataList(resp.getAlramInfo());
            contentLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        } else {
            contentLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }
        refreshListView.onPullDownRefreshComplete();
    }

    @Override
    public void getAlarmListFailed() {
        mLoadingView.showError();
    }

    @Override
    public void updateAlarmSuccess() {
        ToastUtils.showShort(this, "闹钟修改成功");
        mPresenter.getAlarmList(deviceId);
    }

    @Override
    public void deleteAlarmSuccess() {
        ToastUtils.showShort(this, "闹钟删除成功");
        mPresenter.getAlarmList(deviceId);
    }

    @Override
    public void sendAlarmSuccess() {
        hideProgressDilog();
        ToastUtils.showShort(this, "数据提交成功，请五分钟后检查您的座机！");
        mPresenter.getAlarmList(deviceId);
    }

    @Override
    public void sendAlarmFailed() {
        hideProgressDilog();
    }

    @Override
    public void setPresenter(AlarmListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDeleteClick(int index) {
        if(isAdmin){
            new MyAlertDialog(this, R.style.customDialog)
                    .setTitle("温馨提示")
                    .setMessage("是否删除该条远程定时提醒设置？\n删除后点击“同步至座机”生效")
                    .setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mPresenter.deleteAlarm(adapter.getDataList().get(index).getId());
                        }
                    }).showDialog();
        }
    }

    @Override
    public void onCheckBoxChange(int index, boolean isCheck) {
        if(!isAdmin){
            return;
        }
        SaveAlarmReq req = new SaveAlarmReq();
        AlarmInfo bean = adapter.getDataList().get(index);
        req.setAlarm_id(bean.getId());
        req.setDevice_id(deviceId);
        req.setTitle(bean.getTitle());
        req.setWeek(bean.getWeek());
        req.setAlarm_time(bean.getDate());
        req.setState(isCheck?1:0);
        req.setRecord_is_modify(0);
        mPresenter.updateAlarm(req);
    }

    @Override
    public void onVoiceClick(ImageView voiceAnim, int index) {
        this.voiceAnim = voiceAnim;
        startPlay(adapter.getDataList().get(index).getRecord_file());
    }

    private boolean isPlay = false;
    private MediaPlayer player;
    private AnimationDrawable animationDrawable;
    private ImageView voiceAnim;

    void startPlay(String file_url) {
        if (isPlay) {
            releaseMediaPlayer();
            return;
        }
        isPlay = true;
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        player.setOnPreparedListener(this);
        player.setOnErrorListener(this);
        try {
            player.setDataSource(file_url);
            player.prepareAsync();
        } catch (IOException e) {
            Logger.e(TAG, "playMedia Exception：" + e);
        }
        voiceAnim.setBackgroundResource(R.drawable.anim_small_voice);
        animationDrawable = (AnimationDrawable) voiceAnim.getBackground();
        if (animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }

    void releaseMediaPlayer() {
        if (player != null && player.isPlaying()) {
            player.pause();
            player.stop();
            player.release();
            player = null;
            if (animationDrawable != null && animationDrawable.isRunning()) {
                animationDrawable.stop();
                voiceAnim.setBackgroundResource(R.mipmap.btn_timingsound3);
            }
            isPlay = false;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        animationDrawable.start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        isPlay = false;
        animationDrawable.stop();
        if (voiceAnim != null) {
            voiceAnim.setBackgroundResource(R.mipmap.btn_timingsound3);
        }
        player = null;
    }


    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        releaseMediaPlayer();
        isPlay = false;
        player = null;
        return false;
    }
}
