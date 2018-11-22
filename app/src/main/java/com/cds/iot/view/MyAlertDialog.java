package com.cds.iot.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

import com.cds.iot.R;


public class MyAlertDialog extends Dialog {

    protected Context mContext;

    protected LayoutParams mLayoutParams;

    private Button cancelBtn;

    private Button confirmBtn;

    private AppCompatTextView titleTv;

    private AppCompatTextView contentTv;

    private boolean showTitle = false;

    private boolean showMessage = false;

    public MyAlertDialog(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public MyAlertDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    protected MyAlertDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView(context);
    }

    private MyAlertDialog initView(Context context) {
        mContext = context;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawableResource(R.mipmap.transparent_bg);
        Window window = this.getWindow();
        mLayoutParams = window.getAttributes();
        mLayoutParams.alpha = 1f;
        mLayoutParams.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        mLayoutParams.gravity = Gravity.CENTER;
        window.setAttributes(mLayoutParams);

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_alert, null);
        titleTv = (AppCompatTextView) dialogView.findViewById(R.id.title);
        contentTv = (AppCompatTextView) dialogView.findViewById(R.id.content);
        confirmBtn = (Button) dialogView.findViewById(R.id.confirm);
        cancelBtn = (Button) dialogView.findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        setContentView(dialogView);

        return this;
    }

    public MyAlertDialog setTitle(String message) {
        showTitle = true;
        changeViewState();
        titleTv.setText(message);
        return this;
    }

    public MyAlertDialog setMessage(String message) {
        showMessage = true;
        changeViewState();
        contentTv.setText(message);
        return this;
    }

    public MyAlertDialog setMessage(String message, int gravity) {
        showMessage = true;
        changeViewState();
        contentTv.setText(message);
        contentTv.setGravity(gravity);
        return this;
    }

    void changeViewState() {
        if (showTitle) {
            titleTv.setVisibility(View.VISIBLE);
        } else {
            titleTv.setVisibility(View.GONE);
        }
        if (showMessage) {
            contentTv.setVisibility(View.VISIBLE);
        } else {
            contentTv.setVisibility(View.GONE);
        }
    }

    public MyAlertDialog setPositiveButton(String str, final View.OnClickListener listener) {
        confirmBtn.setText(str);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                listener.onClick(view);
            }
        });
        return this;
    }

    public MyAlertDialog setCancelButton(String str, final View.OnClickListener listener) {
        cancelBtn.setText(str);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                listener.onClick(view);
            }
        });
        return this;
    }

    public MyAlertDialog showDialog() {
        show();
        return this;
    }
}
