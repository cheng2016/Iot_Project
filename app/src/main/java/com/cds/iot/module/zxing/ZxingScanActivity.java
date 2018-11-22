package com.cds.iot.module.zxing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.util.ImageUtils;
import com.cds.iot.util.Logger;
import com.cds.iot.util.ToastUtils;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * 二维码扫描界面
 */
public class ZxingScanActivity extends BaseActivity implements QRCodeView.Delegate, View.OnClickListener {
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;
    private ZXingView mZXingView;
    private boolean isOpen = false;
    private TextView lightTv;

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            //光线强度
            float lux = sensorEvent.values[0];
            Logger.i(TAG, "光线传感器得到的光线强度-->" + lux);
            if (lux > 50) {
                if (!isOpen)
                    lightTv.setVisibility(View.GONE);
            } else {
                lightTv.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_zing_scan;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.right_button).setVisibility(View.VISIBLE);
        findViewById(R.id.right_img).setVisibility(View.GONE);
        findViewById(R.id.right_tv).setVisibility(View.VISIBLE);
        findViewById(R.id.right_tv).setOnClickListener(this);
        lightTv = (TextView) findViewById(R.id.light_button);
        lightTv.setOnClickListener(this);
        mZXingView = (ZXingView) findViewById(R.id.zxingview);
        mZXingView.setDelegate(this);
    }

    @Override
    protected void initData() {
        ((TextView) findViewById(R.id.title)).setText("添加设备");
        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); // 获取光线传感器
        if (lightSensor != null) { // 光线传感器存在时
            sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL); // 注册事件监听
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
//        mZXingView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别
        mZXingView.startSpotAndShowRect(); // 显示扫描框，并且延迟0.5秒后开始识别
        mZXingView.startSpot(); // 延迟0.5秒后开始识别

    }

    @Override
    protected void onStop() {
        mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        mZXingView.stopSpot(); // 停止识别
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZXingView.onDestroy(); // 销毁二维码扫描控件
        if (lightSensor != null) {
            sensorManager.unregisterListener(listener);
        }
        super.onDestroy();

    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Logger.i(TAG, "result:" + result);
        vibrate();
        mZXingView.startSpot(); // 延迟0.5秒后开始识别
        if (TextUtils.isEmpty(result)) {
            ToastUtils.showShort(this, "未发现二维码");
            finish();
        }
        Intent intent = new Intent();
        intent.putExtra("qr", result);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mZXingView.startSpotAndShowRect(); // 显示扫描框，并且延迟0.5秒后开始识别
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {
            Uri uri = data.getData();
            String picturePath = ImageUtils.getImageAbsolutePath(this, uri);
            mZXingView.decodeQRCode(picturePath);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.right_tv:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
                break;
            case R.id.light_button:
                if (isOpen) {
                    isOpen = false;
                    mZXingView.closeFlashlight(); // 关闭闪光灯
                    lightTv.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.scan_torch_off), null, null);
                    lightTv.setText("轻触照亮");
                } else {
                    isOpen = true;
                    mZXingView.openFlashlight(); // 打开闪光灯
                    lightTv.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.btn_torch_on), null, null);
                    lightTv.setText("轻触关闭");
                }
                break;
        }
    }
}
