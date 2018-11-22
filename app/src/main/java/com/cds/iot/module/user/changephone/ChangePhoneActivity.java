package com.cds.iot.module.user.changephone;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cds.iot.App;
import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.util.MD5Utils;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.RegexUtils;
import com.cds.iot.util.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 变更手机号界面
 */
public class ChangePhoneActivity extends BaseActivity implements View.OnClickListener, ChangePhoneContract.View {
    @Bind(R.id.message)
    AppCompatTextView messageTv;
    @Bind(R.id.getcode_tv)
    AppCompatTextView getcodeTv;
    @Bind(R.id.phone_edit)
    AppCompatEditText phoneEdit;
    @Bind(R.id.code_edit)
    AppCompatEditText codeEdit;

    @Bind(R.id.step1_layout)
    LinearLayout step1Layout;
    @Bind(R.id.password_edit)
    AppCompatEditText passwordEdit;
    @Bind(R.id.show_password_checkbox)
    CheckBox showPasswordCheckbox;
    @Bind(R.id.next_btn)
    AppCompatButton nextBtn;
    @Bind(R.id.step2_layout)
    LinearLayout step2Layout;

    ChangePhonePresenter mPresenter;



    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_phone;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.commit_btn).setOnClickListener(this);
        findViewById(R.id.getcode_tv).setOnClickListener(this);
        nextBtn.setOnClickListener(this);
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
        new ChangePhonePresenter(this);
        ((TextView) findViewById(R.id.title)).setText("手机号变更");
        if (getIntent() != null && getIntent().getExtras() != null) {
            messageTv.setText(String.format(getResources().getString(R.string.change_phone_messge), getIntent().getStringExtra("phone")));
        }
    }

    @Override
    public void onClick(View view) {
        String phone = phoneEdit.getText().toString();
        String code = codeEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.commit_btn:
                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.showShort(this, "手机号不能为空");
                } else if (TextUtils.isEmpty(code)) {
                    ToastUtils.showShort(this, "验证码不能为空");
                } else {
                    mPresenter.changePhone(phone, code);
                }
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
            case R.id.next_btn:
                if (TextUtils.isEmpty(password)) {
                    ToastUtils.showShort(this, "请先输入登录密码");
                } else {
                    String pwd = PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_PASSWORD, "");
                    password = MD5Utils.MD5(MD5Utils.MD5(password));
                    if(pwd.equals(password)){
                        step1Layout.setVisibility(View.GONE);
                        step2Layout.setVisibility(View.VISIBLE);
                    }else {
                        ToastUtils.showShort(this, "密码错误，请重新输入");
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    @Override
    public void getCodeSuccess() {
        mPresenter.countdown(getcodeTv);
    }

    @Override
    public void changePhoneSuccess() {
        ToastUtils.showShort(App.getInstance(), "手机号更换成功");
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void setPresenter(ChangePhoneContract.Presenter presenter) {
        mPresenter = (ChangePhonePresenter) presenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
