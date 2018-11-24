package com.cds.iot.module.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.data.Constant;
import com.cds.iot.data.entity.WxUserInfoResp;
import com.cds.iot.module.bindthird.BindThirdActivity;
import com.cds.iot.module.forget.ForgetActivity;
import com.cds.iot.module.main.MainActivity;
import com.cds.iot.module.register.RegisterActivity;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.ScreenUtils;
import com.cds.iot.util.ToastUtils;
import com.cds.iot.wxapi.WXEntryActivity;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import butterknife.Bind;

public class LoginActivity extends BaseActivity implements LoginContract.View, View.OnClickListener {
    @Bind(R.id.account)
    AppCompatEditText accountView;
    @Bind(R.id.password)
    AppCompatEditText passwordView;
    @Bind(R.id.show_password_checkbox)
    CheckBox showPasswordCheckbox;
    @Bind(R.id.bg_img)
    ImageView bgImg;

    LoginPresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.register_btn).setOnClickListener(this);
        findViewById(R.id.modify_password_button).setOnClickListener(this);
        findViewById(R.id.logo_img).setOnClickListener(this);
        findViewById(R.id.weixin).setOnClickListener(this);
        showPasswordCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //如果选中，显示密码
                    passwordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordView.setSelection(passwordView.getText().toString().length());
                } else {
                    //否则隐藏密码
                    passwordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordView.setSelection(passwordView.getText().toString().length());
                }

            }
        });
    }

    @Override
    protected void initData() {
        new LoginPresenter(this);

        startBgAnimation();

        String account = PreferenceUtils.getPrefString(this, PreferenceConstants.USER_NAME, "");
        if (!TextUtils.isEmpty(account)) {
            accountView.setText(account);
            accountView.setSelection(account.length());
        }
    }


    private void startBgAnimation() {
        //填充图片
        drawables = new Drawable[ids.length];
        for (int i = 0; i < ids.length; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawables[i] = getDrawable(ids[i]);
            } else {
                drawables[i] = getResources().getDrawable(ids[i]);
            }
        }

        mThread = new Thread(new MyRunnable());
        mThread.start();
    }

    private int change = 0;//记录下标
    private int[] ids = new int[]{R.mipmap.loginbg_fenxiyi, R.mipmap.loginbg_houshijing, R.mipmap.loginbg_shoubiao, R.mipmap.loginbg_zuoji};
    private Drawable[] drawables;//图片集合
    private Thread mThread;//线程
    private boolean mThreadFlag = true;//线程结束标志符

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int duration = msg.arg1;
            TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{drawables[change % ids.length],
                    drawables[(change + 1) % ids.length]});
            change++;//改变标识位置
            bgImg.setImageDrawable(transitionDrawable);
            transitionDrawable.startTransition(duration);
        }
    };

    class MyRunnable implements Runnable {
        @Override
        public void run() {
            //这个while(true)是做死循环
            while (mThreadFlag) {
                int duration = 1600;//改变的间隔
                Message message = handler.obtainMessage();
                message.arg1 = duration;
                handler.sendMessage(message);
                try {
                    Thread.sleep(duration);
                    //隔duration秒发送一次
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.email_sign_in_button:
                String acount = accountView.getText().toString().trim();
                String password = passwordView.getText().toString().trim();
                if (TextUtils.isEmpty(acount)
                        || TextUtils.isEmpty(password)) {
                    ToastUtils.showShort(this, "账户和密码不能为空！");
                } else {
                    if (!RegexUtils.isMobileExact(acount)) {
                        ToastUtils.showShort(this, "请输入正确的账号！");
                    } else {
                        login(acount, password);
                    }
                }
                break;
            case R.id.logo_img:
                login("18202745852", "123");
                break;
            case R.id.register_btn:
                Intent intent = new Intent();
                intent.setClass(this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.modify_password_button:
                Intent i = new Intent();
                i.setClass(this, ForgetActivity.class);
                startActivity(i);
                break;
            case R.id.weixin:
                IWXAPI api = WXAPIFactory.createWXAPI(this, WXEntryActivity.WX_APP_ID, false);
                if (!api.isWXAppInstalled()) {
                    //提醒用户没有按照微信
                    ToastUtils.showShort(this, "请先安装微信客户端");
                    return;
                }
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "wechat_login";
                api.sendReq(req);
                showProgressDilog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mThreadFlag = false;//结束线程
        mPresenter.unsubscribe();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == Constant.REQUEST_WX_LOGIN && resultCode == RESULT_OK) {
            showProgressDilog("第三方登录中...");
            WxUserInfoResp resp = (WxUserInfoResp) intent.getExtras().getSerializable("WxUserInfoResp");
            mPresenter.thirdLogin(resp);
        }
    }

    private void login(String account, String password) {
        KeyboardUtils.hideSoftInput(this);
        if (TextUtils.isEmpty(account)) {
            ToastUtils.showShort(this, "账户不能为空");
        } else if (TextUtils.isEmpty(password)) {
            ToastUtils.showShort(this, "密码不能为空");
        } else {
            showProgressDilog();
            mPresenter.login(account, password);
        }
    }

    @Override
    public void loginSuccess() {
        hideProgressDilog();
        startActivity(new Intent().setClass(LoginActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onEventMainThread(Object event) {
        super.onEventMainThread(event);
        if (event instanceof SendAuth.Resp) {
            SendAuth.Resp resp = (SendAuth.Resp) event;
            mPresenter.getWXUserInfo(this, resp.code);
            showProgressDilog("正在获取微信用户信息...");
        }
    }

    @Override
    public void loginFailed() {
        hideProgressDilog();
    }

    @Override
    public void getWXUserInfoSuccess(WxUserInfoResp resp) {
        showProgressDilog("正在进行第三方登录...");
        mPresenter.thirdLogin(resp);
    }

    @Override
    public void getWXUserInfoFailed() {
        hideProgressDilog();
    }

    @Override
    public void thirdLoginSuccess() {
        hideProgressDilog();
        startActivity(new Intent().setClass(LoginActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void thirdLoginFailed() {
        hideProgressDilog();
    }

    @Override
    public void thirdbind(WxUserInfoResp resp) {
        hideProgressDilog();
        Intent intent = new Intent().setClass(LoginActivity.this, BindThirdActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("WxUserInfoResp", resp);
        intent.putExtras(bundle);
        startActivityForResult(intent, Constant.REQUEST_WX_LOGIN);
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        mPresenter = (LoginPresenter) presenter;
    }
}
