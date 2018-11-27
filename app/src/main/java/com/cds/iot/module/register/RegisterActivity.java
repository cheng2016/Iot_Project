package com.cds.iot.module.register;

import android.content.Intent;
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

import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.module.web.WebActivity;
import com.cds.iot.util.RegexUtils;
import com.cds.iot.util.ResourceUtils;
import com.cds.iot.util.ToastUtils;

import butterknife.Bind;

public class RegisterActivity extends BaseActivity implements RegisterContact.View, View.OnClickListener {
    @Bind(R.id.phone)
    AppCompatEditText phoneEdit;
    @Bind(R.id.password)
    AppCompatEditText passwordEdit;
    @Bind(R.id.code)
    AppCompatEditText codeEdit;
    @Bind(R.id.getcode)
    AppCompatTextView getcodeTv;
    @Bind(R.id.show_password_checkbox)
    CheckBox showPasswordCheckbox;

    RegisterPresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.getcode).setOnClickListener(this);
        findViewById(R.id.register_btn).setOnClickListener(this);
        findViewById(R.id.service_tv).setOnClickListener(this);
        showPasswordCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //如果选中，显示密码
                    passwordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordEdit.setSelection(passwordEdit.getText().toString().length());
                } else {
                    //否则隐藏密码
                    passwordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordEdit.setSelection(passwordEdit.getText().toString().length());
                }

            }
        });
    }

    @Override
    protected void initData() {
        ((TextView) findViewById(R.id.title)).setText("注册");
        new RegisterPresenter(this);
    }

    @Override
    public void onClick(View view) {
        String phone = phoneEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        String code = codeEdit.getText().toString().trim();
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.getcode:
                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.showShort(this, "手机号不能为空");
                } else if (!RegexUtils.isMobileSimple(phone)) {
                    ToastUtils.showShort(this, "请输入正确的手机号码");
                } else {
                    mPresenter.getCode(phone);
                }
                break;
            case R.id.register_btn:
                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.showShort(this, "手机号不能为空");
                } else if (TextUtils.isEmpty(password)) {
                    ToastUtils.showShort(this, "密码不能为空");
                } else if (!RegexUtils.isPwdSimple(password) || password.indexOf(" ") != -1) {
                    ToastUtils.showShort(this, "请输入6-20位数字和字母的组合密码");
                } else if (TextUtils.isEmpty(code)) {
                    ToastUtils.showShort(this, "验证码不能为空");
                } else if (!RegexUtils.isMobileSimple(phone)) {
                    ToastUtils.showShort(this, "请输入正确的手机号码");
                } else {
                    mPresenter.register(phone, password, code);
                }
                break;
            case R.id.service_tv:
                Intent intent = new Intent();
                intent.putExtra("url", ResourceUtils.getProperties(this, "serviceUrl"));
                intent.putExtra("title", "用户协议");
                intent.setClass(this, WebActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    @Override
    public void registerSuccess() {
        ToastUtils.showShort(this, "注册成功");
        finish();
    }

    @Override
    public void getCodeSuccess() {
        mPresenter.countdown(getcodeTv);
    }

    @Override
    public void setPresenter(RegisterContact.Presenter presenter) {
        mPresenter = (RegisterPresenter) presenter;
    }
}
