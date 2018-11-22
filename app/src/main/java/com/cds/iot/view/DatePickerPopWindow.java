package com.cds.iot.view;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.cds.iot.R;
import com.cds.iot.util.Logger;

import java.util.Calendar;

import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/19 14:01
 */
public class DatePickerPopWindow extends PopupWindow implements View.OnClickListener {
    public static final String TAG = "MenuPopWindow";
    private Context context;

    public DatePickerPopWindow(Context context) {
        super(context);
        this.context = context;
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        initalize(year, month);
    }

    public DatePickerPopWindow(Context context, int year, int month) {
        super(context);
        this.context = context;
        initalize(year, month);
    }


    private void initalize(int year, int month) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_date_picker, null);
        DatePicker picker = (DatePicker) view.findViewById(R.id.date_picker);
        picker.setDate(year, month);
        picker.setMode(DPMode.SINGLE);
        setContentView(view);
        initWindow();
    }

    private void initWindow() {
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        int with = (int) (d.widthPixels * 0.3);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        //实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0);
        //设置SelectPicPopupWindow弹出窗体的背景
//        this.setBackgroundDrawable(dw);
//        backgroundAlpha((Activity) context, 1f);//0.0-1.0
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha((Activity) context, 1f);
            }
        });
    }

    //设置添加屏幕的背景透明度
    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    public DatePickerPopWindow showAtBottom(View view) {
        Logger.i(TAG, "width：" + getWidth() + " height：" + getHeight());
        //弹窗位置设置
//        showAsDropDown(view, -200, 20);
        showAsDropDown(view);
        //showAtLocation(view, Gravity.TOP | Gravity.RIGHT, 10, 110);//有偏差
        return this;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.all_message:
                if (listener != null) {
                    listener.onOptionClick(-1);
                }
                dismiss();
                break;
            default:
                break;
        }
    }

    OnOptionClickListener listener;

    public void setListener(OnOptionClickListener listener) {
        this.listener = listener;
    }

    public interface OnOptionClickListener {
        void onOptionClick(int type);
    }
}