package com.cds.iot.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cds.iot.R;

import java.util.Calendar;

import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/19 14:21
 */
public class MyDatePickerDialog extends Dialog {
    Context mContext;

    DatePicker picker;

    WindowManager.LayoutParams mLayoutParams;

    public MyDatePickerDialog(@NonNull Context context) {
        super(context,R.style.myDilogStyle);
        initView(context);
    }

    public MyDatePickerDialog(@NonNull Context context, int themeResId) {
        super(context, R.style.myDilogStyle);
        initView(context);
    }

    protected MyDatePickerDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView(context);
    }

    private MyDatePickerDialog initView(Context context) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = context;
        Window window = this.getWindow();
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0);
        window.setBackgroundDrawable(dw);
        window.setDimAmount(0f);//去掉遮罩层（全透明）
        mLayoutParams = window.getAttributes();
        mLayoutParams.alpha = 1f;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.gravity = Gravity.CENTER;
        window.setAttributes(mLayoutParams);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_date_picker, null);
        picker = (DatePicker) view.findViewById(R.id.date_picker);
        picker.setBackgroundResource(R.drawable.button_plan_shape);
        picker.setTitleBackground(R.drawable.button_top_shape);
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        picker.setDate(year, month);
        picker.setMode(DPMode.SINGLE);
        picker.setTodayDisplay(true);
        picker.setDeferredDisplay(false);
        picker.setHolidayDisplay(false);
        picker.setFestivalDisplay(false);
        picker.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
            @Override
            public void onDatePicked(String dateStr) {
                if (listener != null) {
                    listener.onDateSet(picker, dateStr);
                }
            }
        });
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setContentView(view);

        return this;
    }

    public MyDatePickerDialog setMothOfYear(int year,int month){
        picker.setDate(year, month);
        return this;
    }

    public MyDatePickerDialog setDatePickerListener(OnDateSetListener listener) {
        this.listener = listener;
        return this;
    }

    OnDateSetListener listener;

    public interface OnDateSetListener {
        /**
         * @param view
         * @param dateStr
         */
        void onDateSet(DatePicker view, String dateStr);
    }
}