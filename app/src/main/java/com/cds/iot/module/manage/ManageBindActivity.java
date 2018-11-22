package com.cds.iot.module.manage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.data.Constant;
import com.cds.iot.data.entity.Device;
import com.cds.iot.data.entity.DeviceListResp;
import com.cds.iot.module.adapter.ManageDeviceAdapter;
import com.cds.iot.module.adapter.ManageMenuAdapter;
import com.cds.iot.module.manage.detail.ManageDetailActivity;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;

import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import ezy.ui.layout.LoadingLayout;

/**
 * @author Chengzj
 * @date 2018/9/17 11:18
 * <p>
 * 绑定管理界面
 */
public class ManageBindActivity extends BaseActivity implements View.OnClickListener, ManageBindContract.View {
    @Bind(R.id.menus)
    ListView menuListview;
    @Bind(R.id.devices)
    ListView deviceListview;

    ManageMenuAdapter menuAdapter;
    ManageDeviceAdapter deviceAdapter;

    ManageBindContract.Presenter mPresenter;

    String userId;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manage_bind;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((TextView) findViewById(R.id.title)).setText("绑定管理");
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);

        menuListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                menuAdapter.setSelectItem(index);
                menuAdapter.notifyDataSetChanged();
                if (menuAdapter.getDataList().get(index) != null && menuAdapter.getDataList().get(index).getDevices() != null) {
                    List<Device> list = menuAdapter.getDataList().get(index).getDevices();
                    Iterator<Device> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        Device device = iterator.next();
                        if (!Constant.DEVICE_STATE_BIND.equals(device.getState())) {
                            iterator.remove();
                        }
                    }
                    deviceAdapter.setDataList(list);
                }
            }
        });

        deviceListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<Device> devices = menuAdapter.getDataList().get(menuAdapter.getSelectItem()).getDevices();
                Device device = devices.get(i);
                Intent intent = new Intent().setClass(ManageBindActivity.this, ManageDetailActivity.class);
                intent.putExtra("deviceId", device.getId());
                startActivityForResult(intent, RESULT_FIRST_USER);
            }
        });
    }

    @Override
    protected void initData() {
        new ManageBindPresenter(this);
        menuAdapter = new ManageMenuAdapter(this);
        deviceAdapter = new ManageDeviceAdapter(this);
        menuListview.setAdapter(menuAdapter);
        deviceListview.setAdapter(deviceAdapter);
        userId = PreferenceUtils.getPrefString(this, PreferenceConstants.USER_ID, "");
        mPresenter.getDeviceList(userId);
        mLoadingView.showLoading();
        mLoadingView.setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getDeviceList(userId);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mPresenter.getDeviceList(userId);
            setResult(RESULT_OK);
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
    public void setPresenter(ManageBindContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void getDeviceListSuccess(List<DeviceListResp> resp) {
        mLoadingView.showContent();
        if (resp != null && resp.size() > 0) {
            menuAdapter.setSelectItem(0);
            menuAdapter.setDataList(resp);
            if (resp.get(0).getDevices() != null && resp.get(0).getDevices().size() > 0) {
                List<Device> list = resp.get(0).getDevices();
                Iterator<Device> iterator = list.iterator();
                while (iterator.hasNext()) {
                    Device device = iterator.next();
                    if (!Constant.DEVICE_STATE_BIND.equals(device.getState())) {
                        iterator.remove();
                    }
                }
                deviceAdapter.setDataList(list);
            }
        }
    }

    @Override
    public void getDeviceListFailed() {
        mLoadingView.showError();
    }
}
