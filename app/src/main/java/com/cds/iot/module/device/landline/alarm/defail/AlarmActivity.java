package com.cds.iot.module.device.landline.alarm.defail;

import android.Manifest;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.data.entity.AlarmInfo;
import com.cds.iot.data.entity.SaveAlarmReq;
import com.cds.iot.util.Logger;
import com.cds.iot.util.PermissionHelper;
import com.cds.iot.util.ToastUtils;
import com.cds.iot.view.AudioRecordButton;
import com.cds.iot.view.MyAlertDialog;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import com.google.gson.Gson;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.joda.time.DateTime;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;


/**
 * @Author: chengzj
 * @CreateDate: 2018/11/19 13:48
 * @Version: 3.0.0
 */
public class AlarmActivity extends BaseActivity implements View.OnClickListener, AlarmContract.View, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    @Bind(R.id.right_tv)
    AppCompatTextView rightTv;
    @Bind(R.id.title_edit)
    EditText titleEdit;
    @Bind(R.id.hour)
    WheelView hourView;
    @Bind(R.id.min)
    WheelView minView;

    @Bind(R.id.voice_anim)
    AppCompatImageView voiceAnim;
    @Bind(R.id.voice_tip)
    AppCompatImageView voiceTip;
    @Bind(R.id.length_tv)
    TextView lengthTv;

    @Bind(R.id.voice_layout)
    RelativeLayout voiceLayout;
    @Bind(R.id.voice_empty_layout)
    LinearLayout voiceEmptyLayout;

    @Bind(R.id.voice_record_btn)
    AudioRecordButton voiceRecordBtn;
    @Bind(R.id.voice_replace_btn)
    AudioRecordButton voiceReplaceBtn;

    @Bind(R.id.flow_layout)
    TagFlowLayout mFlowLayout;

    TagAdapter mAdapter;

    AlarmContract.Presenter mPresenter;

    String[] mVals = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    DateFormat DATE_FORMAT_MARK = new SimpleDateFormat("HH:mm");
    int hour;
    int min;

    private String deviceId;
    private String alarmId;
    private String week = "0000000";

    private int isModify = 0;
    private boolean hasLocalVoice = false;
    private boolean isPlay = false;
    private MediaPlayer player;
    private AnimationDrawable animationDrawable;

    //录音地址
    private String filePath;
    //网络录音地址
    private String file_url;
    /**
     * 授权处理
     */
    private PermissionHelper mHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_alarm;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((TextView) findViewById(R.id.title)).setText("设置提醒");
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.right_button).setVisibility(View.VISIBLE);
        findViewById(R.id.right_button).setOnClickListener(this);
        findViewById(R.id.right_img).setVisibility(View.GONE);
        rightTv.setVisibility(View.VISIBLE);
        rightTv.setTextColor(getResources().getColor(R.color.theme_color));
        rightTv.setText("保存");
        voiceAnim.setOnClickListener(this);
        mAdapter = new TagAdapter<String>(mVals) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(AlarmActivity.this).inflate(R.layout.item_tv,
                        mFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        };
        mFlowLayout.setAdapter(mAdapter);
        mFlowLayout.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                for (int i = 0; i < 7; i++) {
                    StringBuilder sb = new StringBuilder(week);
                    sb.replace(i, i + 1, "0");
                    for (Integer j : selectPosSet) {
                        sb.replace(j, j + 1, "1");
                    }
                    week = sb.toString();
                }
                Logger.i(TAG, "list：" + new Gson().toJson(selectPosSet) + "，week：" + week);
            }
        });
        hourView.setCyclic(false);
        minView.setCyclic(false);
        List<String> hourItems = new ArrayList<>();
        List<String> minItems = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hourItems.add(i + "  时");
        }
        for (int i = 0; i < 60; i++) {
            minItems.add(i + "  分");
        }
        hourView.setAdapter(new ArrayWheelAdapter(hourItems));
        hourView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                hour = index;
            }
        });
        minView.setAdapter(new ArrayWheelAdapter(minItems));
        minView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                min = index;
            }
        });
        voiceLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDeleteDialog("确认删除此段录音吗？");
                return false;
            }
        });
    }

    @Override
    protected void initData() {
        new AlarmPresenter(this);
        mHelper = new PermissionHelper(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            AlarmInfo bean = (AlarmInfo) getIntent().getExtras().getSerializable("alarmInfo");
            if (bean != null) {
                loadData(bean);
            }
        }
        mHelper.requestPermissions("请授予[录音]，[读写]权限，否则无法录音",
                new PermissionHelper.PermissionListener() {
                    @Override
                    public void doAfterGrand(String... permission) {
                        voiceRecordBtn.setHasRecordPromission(true);
                        voiceRecordBtn.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {
                            @Override
                            public void onFinished(float seconds, String path) {
                                loadRecordData(seconds,path);
                            }
                        });
                        voiceReplaceBtn.setHasRecordPromission(true);
                        voiceReplaceBtn.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {
                            @Override
                            public void onFinished(float seconds, String path) {
                                loadRecordData(seconds,path);
                            }
                        });
                    }
                    @Override
                    public void doAfterDenied(String... permission) {
                        voiceRecordBtn.setHasRecordPromission(false);
                        voiceReplaceBtn.setHasRecordPromission(false);
                        Toast.makeText(AlarmActivity.this, "请授权,否则无法录音", Toast.LENGTH_SHORT).show();
                    }
                }, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    void loadRecordData(float seconds, String path) {
        hasLocalVoice = true;
        isModify = 1;
        filePath = path;
        voiceTip.setVisibility(View.VISIBLE);
        lengthTv.setText((int) seconds + "''");
        voiceEmptyLayout.setVisibility(View.GONE);
        voiceLayout.setVisibility(View.VISIBLE);
    }

    void loadData(AlarmInfo bean) {
        alarmId = bean.getId();
        titleEdit.setText(bean.getTitle());
        titleEdit.setSelection(bean.getTitle().length());
        try {
            Date date = DATE_FORMAT_MARK.parse(bean.getDate());
            hour = date.getHours();
            min = date.getMinutes();
            hourView.setCurrentItem(hour);
            minView.setCurrentItem(min);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(bean.getRecord_file())) {
            voiceEmptyLayout.setVisibility(View.VISIBLE);
            voiceLayout.setVisibility(View.GONE);
        } else {
            voiceEmptyLayout.setVisibility(View.GONE);
            voiceLayout.setVisibility(View.VISIBLE);
            voiceTip.setVisibility(View.GONE);
            lengthTv.setText(bean.getRecord_duration());
        }
        file_url = bean.getRecord_file();
        Set<Integer> integerSet = new HashSet<>();
        for (int i = 0; i < 7; i++) {
            if ("1".equals(bean.getWeek().substring(i, i + 1))) {
                integerSet.add(i);
            }
        }
        mAdapter.setSelectedList(integerSet);
        week = bean.getWeek();
    }

    void showDeleteDialog(String message) {
        new MyAlertDialog(this, R.style.customDialog)
                .setTitle(message)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            filePath = null;
                            hasLocalVoice =false;
                            isModify = 1;
                            voiceLayout.setVisibility(View.GONE);
                            voiceEmptyLayout.setVisibility(View.VISIBLE);
                    }
                }).showDialog();
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
                saveAlarmInfo();
                break;
            case R.id.voice_anim:
                startPlay();
                break;
            default:
                break;
        }
    }

    void saveAlarmInfo() {
        String title = titleEdit.getText().toString();
        if (TextUtils.isEmpty(title)) {
            ToastUtils.showShort(this, "标题不能为空");
            return;
        }
        DateTime dt = new DateTime(2018, 8, 8, hour, min);
        String time = DATE_FORMAT_MARK.format(dt.toDate());
        Logger.i(TAG, "time：" + time);
        SaveAlarmReq req = new SaveAlarmReq();
        req.setAlarm_id(alarmId);
        req.setDevice_id(deviceId);
        req.setTitle(title);
        req.setWeek(week);
        req.setAlarm_time(time);
        req.setState(0);
        req.setRecord_is_modify(isModify);
        if (hasLocalVoice) {
            if (TextUtils.isEmpty(file_url)) {
                showProgressDilog();
                mPresenter.saveAlarmInfo(req, filePath);
            } else {
                showReplaceDialog(req);
            }
        } else {
            mPresenter.saveAlarmInfo(req, "");
        }
    }

    void showReplaceDialog(SaveAlarmReq req) {
        new MyAlertDialog(this)
                .setTitle("提示")
                .setMessage("录音文件已更改，是否上传？")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showProgressDilog();
                        mPresenter.saveAlarmInfo(req, filePath);
                    }
                }).showDialog();
    }

    @Override
    public void saveAlarmInfoSuccess() {
        if (TextUtils.isEmpty(alarmId)) {
            ToastUtils.showShort(this, "添加闹钟成功");
        } else {
            ToastUtils.showShort(this, "修改闹钟成功");
        }
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void saveAlarmInfoFailed() {
        hideProgressDilog();
    }

    void startPlay() {
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
            if (hasLocalVoice) {
                player.setDataSource(filePath);
            } else {
                player.setDataSource(file_url);
            }
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

    @Override
    public void setPresenter(AlarmContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
