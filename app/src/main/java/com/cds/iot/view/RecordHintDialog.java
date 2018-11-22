package com.cds.iot.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.cds.iot.R;

import java.util.Random;

/**
 * @author Chengzj
 * @date 2018/10/9 15:36
 */
public class RecordHintDialog extends Dialog {

    protected Context mContext;

    protected LayoutParams mLayoutParams;

    //存储很多张话筒图片的数组
    private Drawable[] micImages;
    //话筒的图片
    private ImageView micImage;
    //到计时时间
    TextView recordingHint;

    public RecordHintDialog(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public RecordHintDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    protected RecordHintDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView(context);
    }

    private RecordHintDialog initView(Context context) {
        mContext = context;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawableResource(R.mipmap.transparent_bg);
        Window window = this.getWindow();
        mLayoutParams = window.getAttributes();
        mLayoutParams.alpha = 0.9f;
        window.setAttributes(mLayoutParams);
        mLayoutParams.height = LayoutParams.MATCH_PARENT;
        mLayoutParams.width = LayoutParams.WRAP_CONTENT;
        mLayoutParams.gravity = Gravity.CENTER;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_record, null);
        micImage = (ImageView) dialogView.findViewById(R.id.mic_image);
        recordingHint = (TextView) dialogView.findViewById(R.id.recording_hint);
        micImages = new Drawable[]{
                getContext().getResources().getDrawable(R.drawable.rec_icn01),
                getContext().getResources().getDrawable(R.drawable.rec_icn02),
                getContext().getResources().getDrawable(R.drawable.rec_icn03),
                getContext().getResources().getDrawable(R.drawable.rec_icn04),
                getContext().getResources().getDrawable(R.drawable.rec_icn05),
                getContext().getResources().getDrawable(R.drawable.rec_icn06),
                getContext().getResources().getDrawable(R.drawable.rec_icn07)};
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(dialogView);

        return this;
    }

    public void moveUpToCancel() {
        recordingHint.setText(getContext().getResources().getString(R.string.move_up_to_cancel));
        recordingHint.setBackgroundColor(Color.TRANSPARENT);
        micImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.rec_icn01));
    }

    public void releaseToCancel() {
        recordingHint.setText(getContext().getResources().getString(R.string.release_to_cancel));
        recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
        micImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.icn_recordcancel));
    }

    public void tooShort() {
        recordingHint.setText(getContext().getResources().getString(R.string.time_too_short));
        recordingHint.setBackgroundColor(Color.TRANSPARENT);
        micImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.icn_recordfail));
    }


    public void setImage(Drawable drawable) {
        micImage.setImageDrawable(drawable);
    }

    public void setImage(int i) {
        micImage.setImageDrawable(micImages[i]);
    }

    public Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            //根据mHandler发送what的大小决定话筒的图片是哪一张
            //说话声音越大,发送过来what值越大
            if (what > 8) {
                what = new Random().nextInt(8);
            }
            micImage.setImageDrawable(micImages[what]);
        }
    };

    public void dismissDialog(){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        },1300);
    }
}
