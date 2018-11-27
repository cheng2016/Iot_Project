package com.cds.iot.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.cds.iot.R;
import com.cds.iot.util.DeviceUtils;
import com.cds.iot.util.record.AuditRecorderConfiguration;
import com.cds.iot.util.record.DialogManager;
import com.cds.iot.util.record.ExtAudioRecorder;
import com.cds.iot.util.record.FailRecorder;

import java.io.File;
import java.util.Random;

//录音按钮核心类，包括点击、响应、与弹出对话框交互等操作。
public class AudioRecordButton extends android.support.v7.widget.AppCompatButton {
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
    // 正在录音标记
    private boolean isRecording = false;
    //录音对话框
    private DialogManager mDialogManager;
    // 核心录音类
    private ExtAudioRecorder mAudioManager;
    //录音地址
    private String filePath = Environment.getExternalStorageDirectory() + File.separator + "telephone_media" + new Random().nextInt() + ".wav";

    //当前录音时长
    private float mTime = 0;
    // 是否触发了onlongclick，准备好了
    private boolean mReady;
    //标记是否强制终止
    private boolean isOverTime = false;
    //最大录音时长（单位:s）。def:60s
    private int mMaxRecordTime = 10;

    //上下文
    Context mContext;
    //震动类
    private Vibrator vibrator;
    //提醒倒计时
    private int mRemainedTime = 5;
    //设置是否允许录音,这个是是否有录音权限
    private boolean mHasRecordPromission = true;

    public boolean isHasRecordPromission() {
        return mHasRecordPromission;
    }

    public void setHasRecordPromission(boolean hasRecordPromission) {
        this.mHasRecordPromission = hasRecordPromission;
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    public AudioRecordButton(Context context) {
        this(context, null);
    }

    public AudioRecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        //初始化语音对话框
        mDialogManager = new DialogManager(getContext());

        AuditRecorderConfiguration configuration = new AuditRecorderConfiguration.Builder()
                .recorderListener(listener)
                .handler(handler)
                .uncompressed(true)
                .builder();
        mAudioManager = new ExtAudioRecorder(configuration);

        //有同学反应长按响应时间反馈不及时，我们将这一块动作放到DOWN事件中。去onTouchEvent方法查看
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isHasRecordPromission()) {
                    if(prepareAudio()){
                        mReady = true;
                        changeState(STATE_RECORDING);
                    }
                    return false;
                } else {
                    return true;
                }
            }
        });
    }

    boolean prepareAudio() {
        //检查sdcard
        if (!DeviceUtils.isExitsSdcard()) {
            String needSd = getResources().getString(R.string.send_voice_need_sdcard_support);
            Toast.makeText(getContext(), needSd, Toast.LENGTH_SHORT).show();
        }
        // 设置输出文件
        mAudioManager.setOutputFile(filePath);
        mAudioManager.prepare();
        mAudioManager.start();
        //弹出dialog
        if (mAudioManager.getState() != ExtAudioRecorder.State.ERROR) {
            mStateHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
            return true;
        }
        return false;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mCurrentState == STATE_RECORDING){
                mDialogManager.setImage(msg.what);
            }
        }
    };

    /**
     * 录音失败的提示
     */
    ExtAudioRecorder.RecorderListener listener = new ExtAudioRecorder.RecorderListener() {
        @Override
        public void recordFailed(FailRecorder failRecorder) {
            if (failRecorder.getType() == FailRecorder.FailType.NO_PERMISSION) {
                Log.e(TAG, "未获取录音权限！");
                Toast.makeText(getContext(), "未获取录音权限", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "发生了未知错误！");
                Toast.makeText(getContext(), "发生了未知错误", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public interface AudioFinishRecorderListener {
        void onFinished(float seconds, String filePath);
    }

    private AudioFinishRecorderListener mListener;

    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener) {
        mListener = listener;
    }

    // 获取音量大小的runnable
    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecording) {
                try {
                    //最长mMaxRecordTimes
                    if (mTime > mMaxRecordTime) {
                        mStateHandler.sendEmptyMessage(MSG_VOICE_STOP);
                        return;
                    }
                    Thread.sleep(100);
                    mTime += 0.1f;
                    mStateHandler.sendEmptyMessage(MSG_VOICE_CHANGE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    // 三个状态
    private static final int MSG_AUDIO_PREPARED = 0X110;
    private static final int MSG_VOICE_CHANGE = 0X111;
    private static final int MSG_DIALOG_DIMISS = 0X112;

    @SuppressLint("HandlerLeak")
    private final Handler mStateHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    // 显示应该是在audio end prepare之后回调
                    mDialogManager.showRecordingDialog();
                    isRecording = true;
                    new Thread(mGetVoiceLevelRunnable).start();
                    // 需要开启一个线程来变换音量
                    break;
                case MSG_VOICE_CHANGE:
                    //剩余10s
                    showRemainedTime();
                    break;
                case MSG_DIALOG_DIMISS:
                    mDialogManager.dimissDialog();
                    reset();// 恢复标志位
                    break;
                case MSG_VOICE_STOP:
                    isOverTime = true;//超时
                    mDialogManager.dimissDialog();
                    mTime = mAudioManager.stop();
                    mAudioManager.reset();
                    mListener.onFinished(mTime, filePath);
                    reset();// 恢复标志位
                    break;

            }
        }
    };
    //是否触发过震动
    boolean isShcok;

    private void showRemainedTime() {
        //倒计时
        int remainTime = (int) (mMaxRecordTime - mTime);
        if (remainTime < mRemainedTime) {
            if (!isShcok) {
                isShcok = true;
                doShock();
            }
            mDialogManager.getTipsTxt().setText("还可以说" + remainTime + "秒  ");
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

    //手指滑动监听
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                //不想要父视图拦截触摸事件
                getParent().requestDisallowInterceptTouchEvent(true);
                if (isRecording) {
                    // 根据x，y来判断用户是否想要取消
                    if (wantToCancel(x, y)) {
                        changeState(STATE_WANT_TO_CANCEL);
                    } else {
                        if (!isOverTime)
                            changeState(STATE_RECORDING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                // 首先判断是否有触发onlongclick事件，没有的话直接返回reset
                if (!mReady) {
                    reset();
                    return super.onTouchEvent(event);
                }
                // 如果按的时间太短，还没准备好或者时间录制太短，就离开了，则显示这个dialog
                if (!isRecording || mTime < 1f) {
                    mDialogManager.tooShort();
                    // cancel
                    mAudioManager.discardRecording();
                    mStateHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1000);// 持续1.3s
                } else if (mCurrentState == STATE_RECORDING) {
                    //正常录制结束
                    if (isOverTime) {
                        return super.onTouchEvent(event);//超时
                    }
                    mDialogManager.dimissDialog();
                    mAudioManager.stop();
                    if (mListener != null) {// 并且callbackActivity，保存录音
                        mListener.onFinished(mTime, filePath);
                    }
                } else if (mCurrentState == STATE_WANT_TO_CANCEL) {
                    // cancel
                    mDialogManager.dimissDialog();
                    mAudioManager.discardRecording();
                }
                mAudioManager.reset();
                reset();// 恢复标志位
                break;
        }
        return super.onTouchEvent(event);
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

    private boolean wantToCancel(int x, int y) {
        if (x < 0 || x > getWidth()) {// 判断是否在左边，右边，上边，下边
            return true;
        }
        if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }

        return false;
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
                        mDialogManager.recording();
                    }
                    break;
                case STATE_WANT_TO_CANCEL:
//                    setText(R.string.release_cancel);//松开取消
                    // dialog want to cancel
                    mDialogManager.wantToCancel();
                    break;
            }
        }
    }

    @Override
    public boolean onPreDraw() {
        return false;
    }

    public int getMaxRecordTime() {
        return mMaxRecordTime;
    }

    public void setMaxRecordTime(int maxRecordTime) {
        mMaxRecordTime = maxRecordTime;
    }
}
