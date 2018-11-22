package com.cds.iot.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cds.iot.R;

public class PhoneAlertDialog extends Dialog implements View.OnClickListener {

  protected Context mContext;

  protected WindowManager.LayoutParams mLayoutParams;

  private Button cancelBtn;

  private Button confirmBtn;

  private TextView titleTv;

  private CheckBox checkBox1, checkBox2, checkBox3;

  private FrameLayout layout1, layout2, layout3;

  private int index = 0;

  public PhoneAlertDialog(@NonNull Context context) {
    super(context);
    initView(context);
  }

  public PhoneAlertDialog(@NonNull Context context, String phone) {
    super(context);
    initView(context);
    if(phone.equals("110")){
      checkBox1.setEnabled(true);
    }else if(phone.equals("120")){
      checkBox2.setEnabled(true);
    }else if(phone.equals("119")){
      checkBox3.setEnabled(true);
    }
  }

  public PhoneAlertDialog(@NonNull Context context, int themeResId) {
    super(context, themeResId);
    initView(context);
  }

  protected PhoneAlertDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
    super(context, cancelable, cancelListener);
    initView(context);
  }

  private PhoneAlertDialog initView(Context context) {
    mContext = context;
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    this.getWindow().setBackgroundDrawableResource(R.mipmap.transparent_bg);
    Window window = this.getWindow();
    mLayoutParams = window.getAttributes();
    mLayoutParams.alpha = 1f;
    window.setAttributes(mLayoutParams);
    mLayoutParams.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
    mLayoutParams.gravity = Gravity.CENTER;

    View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_phone_alert, null);
    titleTv = (TextView) dialogView.findViewById(R.id.title);


    layout1 = (FrameLayout) dialogView.findViewById(R.id.layout_1);
    layout2 = (FrameLayout) dialogView.findViewById(R.id.layout_2);
    layout3 = (FrameLayout) dialogView.findViewById(R.id.layout_3);

    layout1.setOnClickListener(this);
    layout2.setOnClickListener(this);
    layout3.setOnClickListener(this);

    checkBox1 = (CheckBox) dialogView.findViewById(R.id.checkbox_1);
    checkBox2 = (CheckBox) dialogView.findViewById(R.id.checkbox_2);
    checkBox3 = (CheckBox) dialogView.findViewById(R.id.checkbox_3);

    confirmBtn = (Button) dialogView.findViewById(R.id.confirm);
    cancelBtn = (Button) dialogView.findViewById(R.id.cancel);
    cancelBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dismiss();
      }
    });
    setCancelable(false);
    setCanceledOnTouchOutside(false);
    setContentView(dialogView);

    return this;
  }

  public PhoneAlertDialog setTitle(String message) {
    titleTv.setVisibility(View.VISIBLE);
    titleTv.setText(message);
    return this;
  }

  public PhoneAlertDialog setMessage(String message) {
    return this;
  }

  public PhoneAlertDialog setPositiveButton(String str, final OnPhoneClickListener listener) {
    confirmBtn.setText(str);
    confirmBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        listener.onClick(view, index);
        dismiss();
      }
    });
    return this;
  }


  public PhoneAlertDialog setCancelButton(String str, final View.OnClickListener listener) {
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

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.layout_1:
        checkBox1.setEnabled(true);
        checkBox2.setEnabled(false);
        checkBox3.setEnabled(false);
        index = 0;
        break;
      case R.id.layout_2:
        checkBox1.setEnabled(false);
        checkBox2.setEnabled(true);
        checkBox3.setEnabled(false);
        index = 1;
        break;
      case R.id.layout_3:
        checkBox1.setEnabled(false);
        checkBox2.setEnabled(false);
        checkBox3.setEnabled(true);
        index = 2;
        break;
      default:
        break;
    }
  }

  public PhoneAlertDialog showDialog(){
    show();
    return this;
  }


  void resetState() {
    checkBox1.setEnabled(false);
    checkBox2.setEnabled(false);
    checkBox3.setEnabled(false);
  }

  public interface OnPhoneClickListener {
    void onClick(View var1, int index);
  }
}
