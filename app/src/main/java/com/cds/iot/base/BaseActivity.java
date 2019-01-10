package com.cds.iot.base;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cds.iot.service.SocketService;
import com.cds.iot.util.AppManager;
import com.cds.iot.util.Logger;
import com.cds.iot.view.ProgressDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import ezy.ui.layout.LoadingLayout;

/**
 * Created by chengzj on 2017/6/17.
 */

public abstract class BaseActivity extends AppCompatActivity {
    public String TAG = "";
    protected SocketService mSocketService;

    private ProgressDialog progressDialog;

    protected LoadingLayout mLoadingView;

    protected ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SocketService.MyBinder binder = (SocketService.MyBinder) service;
            mSocketService = binder.getService();
            Logger.d(TAG, "onServiceConnected");
        }

        //client 和service连接意外丢失时，会调用该方法
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.d(TAG, "onServiceDisconnected");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        TAG = this.getClass().getSimpleName();
        super.onCreate(savedInstanceState);
        Logger.d(TAG,"onCreate");
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        AppManager.getInstance().addActivity(this);
        //绑定服务
        Intent intent = new Intent(this, SocketService.class);
        intent.putExtra("from", TAG);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        changeStatusBarTextColor(true);
        initView(savedInstanceState);
        //添加通用加载视图模块
        mLoadingView = LoadingLayout.wrap(this);
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.d(TAG,"onNewIntent");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d(TAG,"onActivityResult");
    }

    /**
     * 改变状态栏字体颜色值
     *
     * @param isBlack
     */
    private void changeStatusBarTextColor(boolean isBlack) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (isBlack) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//设置状态栏黑色字体
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);//恢复状态栏白色字体
            }
        }
    }

    protected abstract int getLayoutId();

    protected abstract void initView(Bundle savedInstanceState);

    protected abstract void initData();

    public void showProgressDilog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage("加载中...");
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void showProgressDilog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(message);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void hideProgressDilog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void postEvent(Object event){
        EventBus.getDefault().post(event);
    }

    @Subscribe
    public void onEventMainThread(Object event) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d(TAG,"onDestroy");
        ButterKnife.unbind(this);
        //解绑服务
        unbindService(mServiceConnection);
        EventBus.getDefault().unregister(this);
        AppManager.getInstance().finishActivity(this);
    }
}
