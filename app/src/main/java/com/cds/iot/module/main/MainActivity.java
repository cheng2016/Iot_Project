package com.cds.iot.module.main;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;

import com.cds.iot.App;
import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.data.Constant;
import com.cds.iot.data.entity.LoginOut;
import com.cds.iot.module.adapter.pager.MainPagerAdapter;
import com.cds.iot.module.device.DeviceFragment;
import com.cds.iot.module.login.LoginActivity;
import com.cds.iot.module.message.MessageFragment;
import com.cds.iot.util.Logger;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.ToastUtils;

import butterknife.Bind;

public class MainActivity extends BaseActivity {
    public static final String MESSAGE_BROADCAST_ACTION = "com.cds.iot.module.message.action";

    @Bind(R.id.vp_horizontal_ntb)
    ViewPager viewPager;
    @Bind(R.id.radio_group)
    RadioGroup radioGroup;
    @Bind(R.id.message_tip)
    View messageTipImg;

    MainPagerAdapter mMainPagerAdapter;

    MessageReceiver messageReceiver;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mMainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mMainPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
//                radioGroup.check(position + 1);
                switch (position) {
                    case 0:
                        radioGroup.check(R.id.device);
                        break;
                    case 1:
                        radioGroup.check(R.id.message);
                        messageTipImg.setVisibility(View.INVISIBLE);
                        clearNotificaction();
                        break;
                    case 2:
                        radioGroup.check(R.id.me);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        radioGroup.check(R.id.device);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                Logger.i(TAG, "onCheckedChanged：" + checkedId);
//                viewPager.setCurrentItem(checkedId - 1);
                switch (checkedId) {
                    case R.id.device: //设备
                        checkedId = 0;
                        break;
                    case R.id.message: //消息
                        checkedId = 1;
                        messageTipImg.setVisibility(View.INVISIBLE);
                        clearNotificaction();
                        break;
                    case R.id.me: //我的
                        checkedId = 2;
                        break;
                    default:
                        break;
                }
                viewPager.setCurrentItem(checkedId);
            }
        });
    }

    @Override
    protected void initData() {
        //注册广播
        registerReceiver();
        //请求连接tcp服务器
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSocketService != null) {
                    mSocketService.initPushService();
                }
            }
        }, 300);
    }

    @Override
    public void onEventMainThread(Object event) {
        super.onEventMainThread(event);
        if(event instanceof LoginOut){
            //清空登录信息
            PreferenceUtils.setPrefString(App.getInstance(), PreferenceConstants.USER_PASSWORD, "");
            PreferenceUtils.setPrefString(App.getInstance(), PreferenceConstants.USER_ID, "");
            closeNetty();
            Intent i = new Intent().setClass(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        viewPager.setCurrentItem(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(messageReceiver);
        closeNetty();
        //程序退出前清除所有推送
        clearNotificaction();
    }

    private void clearNotificaction() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancelAll();
    }

    private void registerReceiver() {
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(MESSAGE_BROADCAST_ACTION);
        messageReceiver = new MessageReceiver();
        registerReceiver(messageReceiver, iFilter);
    }

    /**
     * 构造消息广播监听类，收到消息后刷新该页面
     */
    public class MessageReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MESSAGE_BROADCAST_ACTION)
                    && mMainPagerAdapter.getmFragments().length > 0
                    && mMainPagerAdapter.getmFragments()[1] instanceof MessageFragment) {
                MessageFragment fragment = (MessageFragment) mMainPagerAdapter.getmFragments()[1];
                fragment.queryMessage();
                messageTipImg.setVisibility(View.VISIBLE);
                int type = Integer.parseInt(intent.getStringExtra("messageType"));
                switch (type) {
                    case Constant.MESSAGE_TYPE_USER_ADD_APPLY_REFUSE:
                    case Constant.MESSAGE_TYPE_USER_ADD_APPLY_AGREE:
                    case Constant.MESSAGE_TYPE_ADMIN_REMOVE_USER_DEVICE:
                        DeviceFragment dFragment = (DeviceFragment) mMainPagerAdapter.getmFragments()[0];
                        dFragment.queryDeviceList();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    //双击返回键 退出
    //----------------------------------------------------------------------------------------------
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            ToastUtils.showShort(this, "再次点击返回键退出");
        }
        mBackPressed = System.currentTimeMillis();
    }

    void closeNetty(){
        if (null != mSocketService) {
            mSocketService.shutdownNetty();
        }
    }
}
