package com.cds.iot.module.adddevice;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.data.entity.DeviceCode;
import com.cds.iot.module.zxing.ZxingScanActivity;
import com.cds.iot.util.ToastUtils;
import com.cds.iot.view.MyAlertDialog;

import butterknife.Bind;

public class AddDeviceActivity extends BaseActivity implements View.OnClickListener, AddDeviceContract.View {
    public final static int REQUEST_SCAN_QR = 0X12;

    @Bind(R.id.device_id_edit)
    EditText deviceIdEdit;
    @Bind(R.id.device_name_edit)
    EditText deviceNameEdit;

    AddDevicePresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.scan_img).setOnClickListener(this);
        findViewById(R.id.add_btn).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        new AddDevicePresenter(this);
        ((TextView) findViewById(R.id.title)).setText("添加设备");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.scan_img:
                Intent intent = new Intent().setClass(this, ZxingScanActivity.class);
                startActivityForResult(intent, REQUEST_SCAN_QR);
                break;
            case R.id.add_btn:
                String deviceId = deviceIdEdit.getText().toString();
                String deviceName = deviceNameEdit.getText().toString();
                if (TextUtils.isEmpty(deviceId)) {
                    ToastUtils.showShort(this, "设备id不能为空");
                } else if (TextUtils.isEmpty(deviceName)) {
                    ToastUtils.showShort(this, "设备名不能为空");
                } else {
                    mPresenter.validateDevice(deviceId, "");
                    KeyboardUtils.hideSoftInput(view);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCAN_QR && resultCode == RESULT_OK) {
            String qr = data.getStringExtra("qr");
            if (qr.startsWith("http")) {
                mPresenter.deviceCode(qr);
            } else {
                String[] split = qr.split("_");
                if (split.length > 0) {
                    deviceIdEdit.setText(split[1]);
                } else {
                    deviceIdEdit.setText(qr);
                }
            }
        }
    }

    @Override
    public void validateDeviceSuccess() {
        mPresenter.addDevice(deviceIdEdit.getText().toString(), deviceNameEdit.getText().toString());
    }

    @Override
    public void showRequestDialog(String title, String info) {
        new MyAlertDialog(this)
                .setTitle(title)
                .setMessage(info)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPresenter.submitRequest(deviceIdEdit.getText().toString(), deviceNameEdit.getText().toString());
                    }
                }).showDialog();
    }

    @Override
    public void addDeviceSuccess() {
        ToastUtils.showShort(this, "添加设备成功");
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void submitRequestSuccess() {
        ToastUtils.showShort(this, "提交申请成功");
        setResult(RESULT_OK);
    }

    @Override
    public void deviceCodeSuccess(DeviceCode resp) {
        String[] splits = resp.getDevice_code().split("_");
        if (splits.length >= 1)
            deviceIdEdit.setText(splits[1]);
    }

    @Override
    public void setPresenter(AddDeviceContract.Presenter presenter) {
        mPresenter = (AddDevicePresenter) presenter;
    }
}
