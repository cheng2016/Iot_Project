package com.cds.iot.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Animatable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cds.iot.R;

/**
 * @Author: chengzj
 * @CreateDate: 2018/11/27 17:34
 * @Version: 3.0.0
 */
public class ProgressDialog extends Dialog {

    protected Context mContext;

    protected WindowManager.LayoutParams mLayoutParams;

    /**进度条*/
    private ProgressBar mProgressBar;
    private TextView messageTv;

    private boolean showTitle = false;

    private boolean showMessage = false;

    public ProgressDialog(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public ProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    protected ProgressDialog(@NonNull Context context, boolean cancelable, @Nullable DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView(context);
    }

    private ProgressDialog initView(Context context) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = context;
        Window window = this.getWindow();
        window.setBackgroundDrawableResource(R.color.transparent);
        mLayoutParams = window.getAttributes();
        mLayoutParams.alpha = 0.9f;
        window.setAttributes(mLayoutParams);
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.gravity = Gravity.CENTER;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_progress, null);
        mProgressBar = dialogView.findViewById(R.id.progress);
        messageTv = (TextView) dialogView.findViewById(R.id.message);
//        setCancelable(false);
//        setCanceledOnTouchOutside(false);
        setContentView(dialogView);
        return this;
    }

    public ProgressDialog setMessage(String message) {
        showTitle = true;
        messageTv.setText(message);
        return this;
    }

    @Override
    public void show() {
        super.show();
//        ((Animatable)mProgressBar.getDrawable()).start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
//        ((Animatable)mProgressBar.getDrawable()).stop();
    }
}