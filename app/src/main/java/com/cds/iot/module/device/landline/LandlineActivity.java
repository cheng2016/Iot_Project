package com.cds.iot.module.device.landline;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.module.device.landline.alarm.AlarmListActivity;
import com.cds.iot.module.device.landline.info.LandLineInfoActivity;
import com.cds.iot.module.device.landline.sos.SoSActivity;
import com.cds.iot.module.web.WebActivity;
import com.cds.iot.util.ResourceUtils;
import com.cds.iot.util.StatusBarUtils;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/29 16:05
 * <p>
 * 座机页面
 */
public class LandlineActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.title)
    AppCompatTextView titleTv;
    @Bind(R.id.back_img)
    ImageView backImg;
    @Bind(R.id.name_tv)
    TextView nameTv;

    private String deviceId;

    private String deviceName;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_landline;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        StatusBarUtils.setWindowStatusBarColor(this, getResources().getColor(R.color.theme_color));
        findViewById(R.id.action_bar_container).setBackgroundColor(getResources().getColor(R.color.theme_color));
        titleTv.setText("无线座机");
        backImg.setImageResource(R.mipmap.btn_back_main_white);
        findViewById(R.id.line).setVisibility(View.GONE);
        titleTv.setTextColor(getResources().getColor(R.color.white));
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            deviceName = getIntent().getStringExtra("deviceName");
            nameTv.setText(deviceName);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @OnClick({R.id.sos_layout, R.id.alarm_layout, R.id.help_layout, R.id.device_layout})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("deviceName", deviceName);
        switch (view.getId()) {
            case R.id.sos_layout:
                intent.setClass(this, SoSActivity.class);
                startActivity(intent);
                break;
            case R.id.alarm_layout:
                intent.setClass(this, AlarmListActivity.class);
                startActivity(intent);
                break;
            case R.id.help_layout:
                intent.putExtra("url", ResourceUtils.getProperties(this, "telephoneUrl"));
                intent.putExtra("title", "帮助说明");
                intent.setClass(this, WebActivity.class);
                startActivity(intent);
                break;
            case R.id.device_layout:
                intent.setClass(this, LandLineInfoActivity.class);
                startActivityForResult(intent, 100);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK){
            deviceName = data.getStringExtra("deviceName");
            nameTv.setText(deviceName);
            setResult(RESULT_OK);
        }
    }
}
