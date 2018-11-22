package com.cds.iot.module.scenes.adddevice;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.cds.iot.App;
import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.data.entity.Device;
import com.cds.iot.data.entity.DeviceCode;
import com.cds.iot.module.adapter.ScenesAddDeviceAdapter;
import com.cds.iot.module.zxing.ZxingScanActivity;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.ToastUtils;
import com.cds.iot.view.MyAlertDialog;

import java.util.List;

import butterknife.Bind;

/**
 * 添加场景设备界面
 */
public class AddScenesDeviceActivity extends BaseActivity implements View.OnClickListener, AddScenesDeviceContract.View, AdapterView.OnItemClickListener {
    public final static int REQUEST_SCAN_QR = 0X12;

    @Bind(R.id.device_id_edit)
    EditText deviceIdEdit;
    @Bind(R.id.device_name_edit)
    EditText deviceNameEdit;
    @Bind(R.id.device_gridview)
    GridView deviceGridview;

    AddScenesDevicePresenter mPresenter;

    ScenesAddDeviceAdapter mAdapter;
    List<Device> mDataList;

    String deviceTypeId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scenes_add_device;
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
        new AddScenesDevicePresenter(this);
        ((TextView) findViewById(R.id.title)).setText("添加设备");
        if (getIntent() != null && getIntent().getExtras() != null) {
            deviceTypeId = getIntent().getStringExtra("typeId");
            String userId = PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID, "");
            mPresenter.getDeviceList(userId, deviceTypeId);
        }
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
                    mPresenter.validateDevice(deviceId, deviceTypeId);
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
                deviceIdEdit.setText(qr);
            }
        }
    }

    @Override
    public void getDeviceSuccess(List<Device> deviceList) {
        mDataList = deviceList;
        if (mDataList != null && mDataList.size() > 0) {
            mAdapter = new ScenesAddDeviceAdapter(this);
            deviceGridview.setAdapter(mAdapter);
            deviceGridview.setOnItemClickListener(this);
            mAdapter.updateData(deviceList);
        }
    }

    @Override
    public void validateDeviceSuccess() {
        Device device = new Device();
        device.setName(deviceNameEdit.getText().toString());
        device.setCode(deviceIdEdit.getText().toString());
        device.setType_id(deviceTypeId);
        device.setTag("add");
        postEvent(device);
        finish();
    }

    @Override
    public void submitRequestSuccess() {
        ToastUtils.showShort(this, "提交申请成功");
        finish();
    }

    @Override
    public void deviceCodeSuccess(DeviceCode resp) {
        String[] splits = resp.getDevice_code().split("_");
        if (splits.length >= 1)
            deviceIdEdit.setText(splits[1]);
    }

    @Override
    public void setPresenter(AddScenesDeviceContract.Presenter presenter) {
        mPresenter = (AddScenesDevicePresenter) presenter;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
        deviceIdEdit.setText(mDataList.get(index).getCode() + "");
        deviceNameEdit.setText(mDataList.get(index).getName());
    }
}
