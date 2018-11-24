package com.cds.iot.util.record;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cds.iot.R;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/11/9 10:34
 */
public class DialogManager {
    /**
     * 以下为dialog的初始化控件，包括其中的布局文件
     */

    private Dialog mDialog;

    private Context mContext;

    private ImageView micImage;

    private TextView tipsTxt;

    //存储很多张话筒图片的数组
    private int[] micImages;

    public DialogManager(Context context) {
        mContext = context;
    }

    public void showRecordingDialog() {
        mDialog = new Dialog(mContext, R.style.Theme_audioDialog);
        // 用layoutinflater来引用布局
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_manager, null);
        mDialog.setContentView(view);

        micImage = (ImageView) view.findViewById(R.id.mic_image);
        tipsTxt = (TextView) view.findViewById(R.id.recording_hint);

        micImages = new int[]{
                R.drawable.rec_icn01,
                R.drawable.rec_icn02,
                R.drawable.rec_icn03,
                R.drawable.rec_icn04,
                R.drawable.rec_icn05,
                R.drawable.rec_icn06,
                R.drawable.rec_icn07};

        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    /**
     * 设置正在录音时的dialog界面
     */
    public void recording() {
        if (mDialog != null && mDialog.isShowing()) {
            micImage.setImageResource(R.drawable.rec_icn01);
            tipsTxt.setText(R.string.up_for_cancel);
            tipsTxt.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        }
    }

    /**
     * 取消界面
     */
    public void wantToCancel() {
        if (mDialog != null && mDialog.isShowing()) {
            micImage.setImageResource(R.drawable.icn_recordcancel);
            tipsTxt.setText(R.string.want_to_cancle);
            tipsTxt.setBackgroundColor(mContext.getResources().getColor(R.color.colorRedBg));
        }
    }

    // 时间过短
    public void tooShort() {
        if (mDialog != null && mDialog.isShowing()) {
            micImage.setImageResource(R.drawable.icn_recordfail);
            tipsTxt.setText(R.string.time_too_short);
        }

    }

    // 隐藏dialog
    public void dimissDialog() {

        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public void setImage(int i) {
        micImage.setImageResource(micImages[i]);
    }

    public TextView getTipsTxt() {
        tipsTxt.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        return tipsTxt;
    }

    public void setTipsTxt(TextView tipsTxt) {
        this.tipsTxt = tipsTxt;
    }
}
