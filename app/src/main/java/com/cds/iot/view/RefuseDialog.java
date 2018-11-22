package com.cds.iot.view;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.cds.iot.R;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/23 14:05
 */
public class RefuseDialog {
    private Context context;
    private Dialog dialog;

    private AppCompatTextView titleTv;
    private AppCompatTextView contentTv;

    private Button reapplyBtn;
    private Button releaseBtn;
    private Button cancelBtn;

    private Display display;

    public RefuseDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public RefuseDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.dialog_refuse, null);

        titleTv = (AppCompatTextView) view.findViewById(R.id.title);
        contentTv = (AppCompatTextView) view.findViewById(R.id.content);
        reapplyBtn = (Button) view.findViewById(R.id.reapply);
        releaseBtn = (Button) view.findViewById(R.id.release);
        cancelBtn = (Button) view.findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
//        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.x = 0;
//        lp.y = 0;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);

        return this;
    }

    public RefuseDialog setTitle(String message) {
        titleTv.setText(message);
        return this;
    }

    public RefuseDialog setMessage(String message) {
        contentTv.setText(message);
        return this;
    }


    public RefuseDialog setReapplyButton(final View.OnClickListener listener) {
        reapplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                listener.onClick(view);
            }
        });
        return this;
    }


    public RefuseDialog setReleaseButton(final View.OnClickListener listener) {
        releaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                listener.onClick(view);
            }
        });
        return this;
    }

    public RefuseDialog show(){
        dialog.show();
        return this;
    }
}
