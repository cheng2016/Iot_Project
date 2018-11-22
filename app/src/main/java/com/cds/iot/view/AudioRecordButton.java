package com.cds.iot.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.cds.iot.App;
import com.cds.iot.R;
import com.cds.iot.module.device.landline.record.AuditRecorderConfiguration;
import com.cds.iot.module.device.landline.record.ExtAudioRecorder;
import com.cds.iot.module.device.landline.record.FailRecorder;
import com.cds.iot.util.DeviceUtils;
import com.cds.iot.util.FileUtils;
import com.cds.iot.util.Logger;
import com.cds.iot.util.ToastUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/11/8 17:15
 */
public class AudioRecordButton extends android.support.v7.widget.AppCompatButton implements ExtAudioRecorder.RecorderListener {
    public static final String TAG = "AudioRecordButton";

    //三个对话框的状态常量
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;

    //垂直方向滑动取消的临界距离
    private static final int DISTANCE_Y_CANCEL = 50;
    //取消录音的状态值
    private static final int MSG_VOICE_STOP = 4;
    //当前状态
    private int mCurrentState = STATE_NORMAL;

    //上下文
    Context mContext;
    //震动类
    private Vibrator vibrator;

    private RecordHintDialog mDialogManager;

    //当前录音时长
    private float mTime = 0;
    // 是否触发了onlongclick，准备好了
    private boolean mReady;

    //标记是否强制终止
    private boolean isOverTime = false;
    //最大录音时长（单位:s）。def:60s
    private int mMaxRecordTime = 60;

    // 正在录音标记
    private boolean isRecording = false;

    //是否触发过震动
    boolean isShcok;

    // 获取类的实例
    private ExtAudioRecorder recorder;
    //录音地址
    private String tempFilePath = App.getInstance().getAppCacheDir() + "temp_media.wav";
    private String filePath = App.getInstance().getAppCacheDir() + "telephone_media.wav";

    public AudioRecordButton(Context context) {
        super(context);
    }

    public AudioRecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mReady = true;
                return false;
            }
        });

        AuditRecorderConfiguration configuration = new AuditRecorderConfiguration.Builder()
                .recorderListener(this)
                .handler(handler)
                .uncompressed(true)
                .builder();
        recorder = new ExtAudioRecorder(configuration);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mDialogManager.setImage(msg.what);
        }
    };

    //手指滑动监听
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    // 根据x，y来判断用户是否想要取消
                    if (wantToCancel(x, y)) {
                        changeState(STATE_WANT_TO_CANCEL);
                    } else {
                        if (!isOverTime){
                            changeState(STATE_RECORDING);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                // 首先判断是否有触发onlongclick事件，没有的话直接返回reset
                if (!mReady) {
                    reset();
                    return super.onTouchEvent(event);
                }
                // 如果按的时间太短，还没准备好或者时间录制太短，就离开了，则显示这个dialog
                mTime = recorder.stop();

                if (!isRecording || mTime < 1) {
                    mDialogManager.tooShort();
                    mDialogManager.dismissDialog();
                } else if (mCurrentState == STATE_RECORDING) {//正常录制结束
                    if (isOverTime) return super.onTouchEvent(event);//超时
                    mDialogManager.dismiss();
                    try {
                        FileUtils.copyFileUsingFileStreams(tempFilePath, filePath);
                    } catch (IOException e) {
                        Logger.e(TAG, "stopRecord IOException：" + e);
                    }
                    Logger.d(TAG, "录音成功地址为：" + filePath);
                    ToastUtils.showShort(mContext,"录音成功地址为：" + filePath);

                } else if (mCurrentState == STATE_WANT_TO_CANCEL) {
                    // cancel
                    recorder.discardRecording();
                    mDialogManager.dismissDialog();
                }
                reset();// 恢复标志位
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean wantToCancel(int x, int y) {
        if (x < 0 || x > getWidth()) {// 判断是否在左边，右边，上边，下边
            return true;
        }
        if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }
        return false;
    }

    void startRecord(){
        //检查sdcard
        if (!DeviceUtils.isExitsSdcard()) {
            String needSd = getResources().getString(R.string.send_voice_need_sdcard_support);
            Toast.makeText(getContext(), needSd, Toast.LENGTH_SHORT).show();
        }
        // 设置输出文件
        recorder.setOutputFile(filePath);
        recorder.prepare();
        recorder.start();
        //弹出dialog
        if (recorder.getState() != ExtAudioRecorder.State.ERROR) {
            mDialogManager.show();
            mDialogManager.moveUpToCancel();
        }
    }

    /**
     * 回复标志位以及状态
     */
    private void reset() {
        isRecording = false;
        changeState(STATE_NORMAL);
        mReady = false;
        mTime = 0;
        isOverTime = false;
        isShcok = false;
    }

    private void changeState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (mCurrentState) {
                case STATE_NORMAL:
//                    setText(mContext.getString(R.string.long_click_record));//长按录音
                    break;
                case STATE_RECORDING:
//                    setBackgroundColor(Color.rgb(0xcd, 0xcd, 0xcd));
//                    setText(R.string.hang_up_finsh);//松开结束
//                    setTextColor(Color.WHITE);
                    if (isRecording) {
                        // 复写dialog.recording();
                        startRecord();
                    }
                    break;
                case STATE_WANT_TO_CANCEL:
                    mDialogManager.releaseToCancel();
                    break;
            }
        }
    }

    /*
     * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
     * */
    private void doShock() {
        vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 400, 100, 400};   // 停止 开启 停止 开启
        vibrator.vibrate(pattern, -1);           //重复两次上面的pattern 如果只想震动一次，index设为-1
    }

    @Override
    public void recordFailed(FailRecorder failRecorder) {
        if (failRecorder.getType() == FailRecorder.FailType.NO_PERMISSION) {
//                ToastUtils.showShort(getActivity(), "录音失败，可能是没有给权限");
            Logger.e(TAG, "未获取录音权限！");
        } else {
            Logger.e(TAG, "发生了未知错误！");
        }
    }
}
