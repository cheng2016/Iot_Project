package com.cds.iot.module.setting.modifypwd;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.util.RegexUtils;
import com.cds.iot.util.ToastUtils;

import butterknife.Bind;

public class ModifyPwdActivity extends BaseActivity implements ModifyPwdContract.View, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    @Bind(R.id.old_password_edit)
    AppCompatEditText oldPasswordEdit;
    @Bind(R.id.password_edit)
    AppCompatEditText passwordEdit;
    @Bind(R.id.password_edit2)
    AppCompatEditText passwordEdit2;
    @Bind(R.id.show_password_checkbox)
    CheckBox showPasswordCheckbox;
    @Bind(R.id.show_password_checkbox2)
    CheckBox showPasswordCheckbox2;

    ModifyPwdPresenter mPresenter;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify_password;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.modify_btn).setOnClickListener(this);
        showPasswordCheckbox.setOnCheckedChangeListener(this);
        showPasswordCheckbox2.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {
        new ModifyPwdPresenter(this);
        ((TextView) findViewById(R.id.title)).setText("修改密码");
    }

    @Override
    public void onClick(View view) {
        String oldpassword = oldPasswordEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        String password2 = passwordEdit2.getText().toString().trim();
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.modify_btn:
                if (TextUtils.isEmpty(oldpassword)) {
                    ToastUtils.showShort(this, "旧密码不能为空");
                } else if (TextUtils.isEmpty(password)) {
                    ToastUtils.showShort(this, "新密码不能为空");
                } else if (TextUtils.isEmpty(password2)) {
                    ToastUtils.showShort(this, "请再次输入新密码");
                }else if (!password.equals(password2)) {
                    ToastUtils.showShort(this, "二次新密码输入不一致");
                } else if (!RegexUtils.isPwdSimple(password) || password.indexOf(" ") != -1 || password.indexOf(" ") != -1) {
                    ToastUtils.showShort(this, "请输入6-20位数字和字母的组合密码");
                } else {
                    mPresenter.modifyPwd(oldpassword,password);
                }
                break;
        }
    }

    @Override
    public void modifyPwdSuccess() {
        ToastUtils.showShort(this, "密码修改成功");
        finish();
    }

    @Override
    public void setPresenter(ModifyPwdContract.Presenter presenter) {
        mPresenter = (ModifyPwdPresenter) presenter;
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
