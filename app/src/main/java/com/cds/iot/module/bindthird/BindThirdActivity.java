package com.cds.iot.module.bindthird;

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
import com.cds.iot.data.entity.WxUserInfoResp;
import com.cds.iot.util.RegexUtils;
import com.cds.iot.util.ToastUtils;

import butterknife.Bind;

/**
 * 微信绑定页面
 */
public class BindThirdActivity extends BaseActivity implements BindThirdContract.View, View.OnClickListener {

    @Bind(R.id.name_edit)
    AppCompatEditText nameEdit;
    @Bind(R.id.password_edit)
    AppCompatEditText passwordEdit;
    @Bind(R.id.code_edit)
    AppCompatEditText codeEdit;
    @Bind(R.id.getcode_tv)
    AppCompatTextView getcodeTv;
    @Bind(R.id.show_password_checkbox)
    CheckBox showPasswordCheckbox;

    BindThirdPresenter mPresenter;

    WxUserInfoResp mWxUserInfoResp;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bind;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.getcode_tv).setOnClickListener(this);
        findViewById(R.id.enter_btn).setOnClickListener(this);
        showPasswordCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    //如果选中，显示密码
                    passwordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    //否则隐藏密码
                    passwordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

            }
        });
    }

    @Override
    protected void initData() {
        new BindThirdPresenter(this);
        ((TextView) findViewById(R.id.title)).setText("关联手机号");
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            mWxUserInfoResp = (WxUserInfoResp) bundle.getSerializable("WxUserInfoResp");
        }
    }

    @Override
    public void onClick(View view) {
        String phone = nameEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        String code = codeEdit.getText().toString();
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.getcode_tv:
                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.showShort(this, "手机号不能为空");
                } else if(!RegexUtils.isMobileSimple(phone)){
                    ToastUtils.showShort(this, "请输入正确的手机号码");
                }else {
                    mPresenter.getCode(phone);
                }
                break;
            case R.id.enter_btn:
                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.showShort(this, "手机号不能为空");
                } else if (TextUtils.isEmpty(password)) {
                    ToastUtils.showShort(this, "密码不能为空");
                }  else if(!RegexUtils.isPwdSimple(password)){
                    ToastUtils.showShort(this, "请输入6-20位数字和字母的组合密码");
                } else if (TextUtils.isEmpty(code)) {
                    ToastUtils.showShort(this, "验证码不能为空");
                } else {
                    mPresenter.thirdBind(phone, password, code, mWxUserInfoResp.getUnionid(),mWxUserInfoResp.getNickname(),mWxUserInfoResp.getHeadimgurl());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void thirdbindSuccess() {
        ToastUtils.showShort(this, "第三方绑定成功！");
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("WxUserInfoResp",mWxUserInfoResp);
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void getCodeSuccess() {
        mPresenter.countdown(getcodeTv);
    }

    @Override
    public void setPresenter(BindThirdContract.Presenter presenter) {
        mPresenter = (BindThirdPresenter) presenter;
    }
}
