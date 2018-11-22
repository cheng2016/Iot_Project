package com.cds.iot.module.setting.notify;

import android.app.NotificationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;

import butterknife.Bind;

public class MessageNotifyActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @Bind(R.id.message_switchbtn)
    CheckBox messageSwitchbtn;
    @Bind(R.id.voice_switchbtn)
    CheckBox voiceSwitchbtn;
    @Bind(R.id.vibrate_switchbtn)
    CheckBox vibrateSwitchbtn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message_notify;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        messageSwitchbtn.setOnCheckedChangeListener(this);
        voiceSwitchbtn.setOnCheckedChangeListener(this);
        vibrateSwitchbtn.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {
        ((TextView) findViewById(R.id.title)).setText("消息提醒");
        messageSwitchbtn.setChecked(PreferenceUtils.getPrefBoolean(this, PreferenceConstants.MESSAGE_NOTIFY,true));
        voiceSwitchbtn.setChecked(PreferenceUtils.getPrefBoolean(this, PreferenceConstants.VOICE_NOTIFY,true));
        vibrateSwitchbtn.setChecked(PreferenceUtils.getPrefBoolean(this, PreferenceConstants.VIBRATE_NOTIFY,true));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()){
            case R.id.message_switchbtn:
                if(isChecked){
                    PreferenceUtils.setPrefBoolean(this, PreferenceConstants.MESSAGE_NOTIFY,true);
                }else{
                    PreferenceUtils.setPrefBoolean(this, PreferenceConstants.MESSAGE_NOTIFY,false);
                }
                break;
            case R.id.voice_switchbtn:
                deleteNotificationChannel();
                if(isChecked){
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    ringtone.play();
                    PreferenceUtils.setPrefBoolean(this, PreferenceConstants.VOICE_NOTIFY,true);
                }else{
                    PreferenceUtils.setPrefBoolean(this, PreferenceConstants.VOICE_NOTIFY,false);
                }
                break;
            case R.id.vibrate_switchbtn:
                deleteNotificationChannel();
                if(isChecked){
                    Vibrator vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);
                    PreferenceUtils.setPrefBoolean(this, PreferenceConstants.VIBRATE_NOTIFY,true);
                }else{
                    PreferenceUtils.setPrefBoolean(this, PreferenceConstants.VIBRATE_NOTIFY,false);
                }
                break;
        }
    }

    //清除通知渠道设置
    private void deleteNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = PreferenceUtils.getPrefString(this, PreferenceConstants.CHANNEL_ID,"");
            if(!TextUtils.isEmpty(channelId)){
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.deleteNotificationChannel(channelId);
            }
        }
        PreferenceUtils.setPrefString(this, PreferenceConstants.CHANNEL_ID,"");
    }
}
