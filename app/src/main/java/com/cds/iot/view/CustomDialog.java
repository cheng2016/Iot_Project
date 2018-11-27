package com.cds.iot.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.cds.iot.R;

public class CustomDialog extends Dialog {

    protected Context mContext;

    protected LayoutParams mLayoutParams;

    private Button cancelBtn, confirmBtn, completeBtn;

    private TextView titleTv, contentTv;

    private boolean showTitle = false;

    private boolean showMessage = false;

    public CustomDialog(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public CustomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    protected CustomDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView(context);
    }

    private CustomDialog initView(Context context) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = context;
        Window window = this.getWindow();
        window.setBackgroundDrawableResource(R.color.transparent);
        mLayoutParams = window.getAttributes();
        mLayoutParams.alpha = 0.9f;
        window.setAttributes(mLayoutParams);
        mLayoutParams.width = LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = LayoutParams.WRAP_CONTENT;
        mLayoutParams.gravity = Gravity.CENTER;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_my_custom, null);
        titleTv = (TextView) dialogView.findViewById(R.id.title);
        contentTv = (TextView) dialogView.findViewById(R.id.content);
        confirmBtn = (Button) dialogView.findViewById(R.id.confirm);
        cancelBtn = (Button) dialogView.findViewById(R.id.cancel);
        completeBtn = dialogView.findViewById(R.id.complete);
        setCancelButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
//        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(dialogView);

        return this;
    }

    public CustomDialog setTitle(String message) {
        showTitle = true;
        changeViewState();
        titleTv.setVisibility(View.VISIBLE);
        titleTv.setText(message);
        return this;
    }

    public CustomDialog setTitle(int color, String message) {
        showTitle = true;
        changeViewState();
        titleTv.setVisibility(View.VISIBLE);
        contentTv.setTextColor(color);
        titleTv.setText(message);
        return this;
    }

    public CustomDialog setMessage(String message) {
        showMessage = true;
        changeViewState();
        contentTv.setVisibility(View.VISIBLE);
        contentTv.setText(message);
        return this;
    }

    public CustomDialog setMessage(int color, String message) {
        showMessage = true;
        changeViewState();
        contentTv.setVisibility(View.VISIBLE);
        contentTv.setTextColor(color);
        contentTv.setText(message);
        return this;
    }

    private void changeViewState() {
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

    public CustomDialog setCompleteButton(String str, final View.OnClickListener listener) {
        completeBtn.setVisibility(View.VISIBLE);
        completeBtn.setText(str);
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view);
                dismiss();
            }
        });
        return this;
    }

    public CustomDialog setPositiveButton(String str, final View.OnClickListener listener) {
        confirmBtn.setText(str);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view);
                dismiss();
            }
        });
        return this;
    }

    public CustomDialog setPositiveButton(String str, int color, final View.OnClickListener listener) {
        confirmBtn.setTextColor(color);
        confirmBtn.setText(str);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view);
                dismiss();
            }
        });
        return this;
    }


    public CustomDialog setCancelButton(String str, final View.OnClickListener listener) {
        cancelBtn.setText(str);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view);
                dismiss();
            }
        });
        return this;
    }

    public CustomDialog showDialog() {
        show();
        return this;
    }
}
