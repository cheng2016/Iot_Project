package com.cds.iot.module.forget;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.cds.iot.App;
import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.util.RegexUtils;
import com.cds.iot.util.ToastUtils;

import butterknife.Bind;

public class ForgetActivity extends BaseActivity implements ForgetContract.View, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @Bind(R.id.phone_edit)
    AppCompatEditText phoneEdit;
    @Bind(R.id.password_edit)
    AppCompatEditText passwordEdit;
    @Bind(R.id.password_edit2)
    AppCompatEditText passwordEdit2;
    @Bind(R.id.code_edit)
    AppCompatEditText codeEdit;
    @Bind(R.id.getcode_tv)
    AppCompatTextView getcodeTv;
    @Bind(R.id.show_password_checkbox)
    CheckBox showPasswordCheckbox;
    @Bind(R.id.show_password_checkbox2)
    CheckBox showPasswordCheckbox2;

    ForgetPresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_forget;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.getcode_tv).setOnClickListener(this);
        findViewById(R.id.reset_btn).setOnClickListener(this);
        showPasswordCheckbox.setOnCheckedChangeListener(this);
        showPasswordCheckbox2.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {
        new ForgetPresenter(this);
        ((TextView) findViewById(R.id.title)).setText("找回密码");
    }

    @Override
    public void onClick(View view) {
        String phone = phoneEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString();
        String password2 = passwordEdit2.getText().toString();
        String code = codeEdit.getText().toString().trim();
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.getcode_tv:
                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.showShort(this, "手机号不能为空");
                } else if (!RegexUtils.isMobileSimple(phone)) {
                    ToastUtils.showShort(this, "请输入正确的手机号码");
                } else {
                    mPresenter.getCode(phone);
                }
                break;
            case R.id.reset_btn:
                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.showShort(this, "手机号不能为空");
                } else if (TextUtils.isEmpty(password) || TextUtils.isEmpty(password2)) {
                    ToastUtils.showShort(this, "密码不能为空");
                } else if (!password.equals(password2)) {
                    ToastUtils.showShort(this, "二次密码不一致");
                } else if (!RegexUtils.isPwdSimple(password) || password.indexOf(" ") != -1 || password.indexOf(" ") != -1) {
                    ToastUtils.showShort(this, "请输入6-20位数字和字母的组合密码");
                } else if (TextUtils.isEmpty(code)) {
                    ToastUtils.showShort(this, "验证码不能为空");
                } else if (!RegexUtils.isMobileSimple(phone)) {
                    ToastUtils.showShort(this, "请输入正确的手机号码");
                } else {
                    mPresenter.resetPassword(phone, password, code);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    @Override
    public void resetPasswordSuccess() {
        ToastUtils.showShort(App.getInstance(), "找回密码成功");
        finish();
    }

    @Override
    public void getCodeSuccess() {
        mPresenter.countdown(getcodeTv);
    }

    @Override
    public void setPresenter(ForgetContract.Presenter presenter) {
        mPresenter = (ForgetPresenter) presenter;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.show_password_checkbox:
                if (isChecked) {
                    //如果选中，显示密码
                    passwordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordEdit.setSelection(passwordEdit.getText().toString().length());
                } else {
                    //否则隐藏密码
                    passwordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordEdit.setSelection(passwordEdit.getText().toString().length());
                }
                break;
            case R.id.show_password_checkbox2:
                if (isChecked) {
                    //如果选中，显示密码
                    passwordEdit2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordEdit2.setSelection(passwordEdit2.getText().toString().length());
                } else {
                    //否则隐藏密码
                    passwordEdit2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordEdit2.setSelection(passwordEdit2.getText().toString().length());
                }
                break;
            default:
                break;
        }

    }
}
