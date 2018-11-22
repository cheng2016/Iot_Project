package com.cds.iot.module.manage.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.data.entity.ManageInfo;
import com.cds.iot.module.adapter.ManageUserAdapter;
import com.cds.iot.module.manage.user.ManageUserActivity;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.ToastUtils;
import com.cds.iot.view.CustomDialog;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

import butterknife.Bind;

/**
 * @author Chengzj
 * @date 2018/9/17 11:25
 * <p>
 * 绑定管理详情页面
 */
public class ManageDetailActivity extends BaseActivity implements View.OnClickListener, ManageUserAdapter.OnDeleteListener, ManageDetailContract.View {
    public static final int TRANSTER_DEVICE_CODE = 100;
    @Bind(R.id.right_tv)
    AppCompatTextView rightTv;
    @Bind(R.id.device_id)
    AppCompatTextView deviceIdTv;
    @Bind(R.id.device_name)
    AppCompatTextView deviceNameTv;
    @Bind(R.id.device_img)
    AppCompatImageView deviceImg;
    @Bind(R.id.is_admin_img)
    AppCompatImageView isAdminImg;
    @Bind(R.id.devices)
    GridView deviceGridView;
    @Bind(R.id.release)
    AppCompatButton releaseButton;
    @Bind(R.id.transfer)
    AppCompatButton transferButton;

    ManageDetailContract.Presenter mPresenter;
    ManageUserAdapter adapter;

    String deviceId;
    String userId;

    boolean isAdmin = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manage_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((TextView) findViewById(R.id.title)).setText("绑定管理");
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        releaseButton.setOnClickListener(this);
        transferButton.setOnClickListener(this);
        findViewById(R.id.right_img).setVisibility(View.GONE);
        rightTv = ((AppCompatTextView) findViewById(R.id.right_tv));
        rightTv.setTextColor(getResources().getColor(R.color.theme_color));
        rightTv.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        new ManageDetailPresenter(this);
        adapter = new ManageUserAdapter(this);
        adapter.setListener(this);
        deviceGridView.setAdapter(adapter);
        rightTv.setVisibility(View.VISIBLE);
        rightTv.setText("编辑");
        if (getIntent() != null && getIntent().getExtras() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            userId = PreferenceUtils.getPrefString(this, PreferenceConstants.USER_ID, "");
            mPresenter.getManageInfo(userId, deviceId);
        }
        mLoadingView.showLoading();
        mLoadingView.setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getManageInfo(userId, deviceId);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TRANSTER_DEVICE_CODE && resultCode == RESULT_OK) {
            int updateUserId = data.getIntExtra("userid", 0);
            mPresenter.transferDevice(String.valueOf(updateUserId), deviceId);
        }
    }

    public void showAdminRelieveDeviceDialog() {
        new CustomDialog(this)
                .setTitle(getResources().getColor(R.color.update_text_color), "是否解除该设备？")
                .setMessage(getResources().getColor(R.color.disable_text), "解除后，您将无法使用该设备。解除前选择一位用户作为新任管理员。")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        transferDevice();
                    }
                }).showDialog();
    }

    public void showRelieveDeviceDialog() {
        new CustomDialog(this)
                .setTitle(getResources().getColor(R.color.update_text_color), "确认解除该设备？")
                .setMessage(getResources().getColor(R.color.disable_text), "解除后，您将无法使用该设备。")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.releaseDevice(userId, deviceId);
                    }
                }).showDialog();
    }

    public void showRelieveUserDialog(final String id) {
        new CustomDialog(this)
                .setTitle(getResources().getColor(R.color.update_text_color), "确认解除该用户")
                .setMessage(getResources().getColor(R.color.disable_text), "解除后，该用户无法使用该设备。")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.releaseUser(id, deviceId);
                    }
                }).showDialog();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.right_tv:
                if ("编辑".equals(rightTv.getText().toString())) {
                    rightTv.setText("取消");
                    adapter.setEdit(true);
                    adapter.notifyDataSetChanged();
                    releaseButton.setVisibility(View.VISIBLE);
                    if (adapter.getDataList().size() != 1) {
                        transferButton.setVisibility(View.VISIBLE);
                    } else {
                        transferButton.setVisibility(View.GONE);
                    }
                } else if ("取消".equals(rightTv.getText().toString())) {
                    rightTv.setText("编辑");
                    adapter.setEdit(false);
                    adapter.notifyDataSetChanged();
                    releaseButton.setVisibility(View.GONE);
                    transferButton.setVisibility(View.GONE);
                }
                break;
            case R.id.release:
                if (adapter.getDataList().size() == 1 || !isAdmin) {
                    showRelieveDeviceDialog();
                } else {
                    showAdminRelieveDeviceDialog();
                }
                break;
            case R.id.transfer:
                transferDevice();
                break;
            default:
                break;
        }
    }

    private void transferDevice() {
        Intent intent = new Intent().setClass(this, ManageUserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("users", (Serializable) adapter.getDataList());
        intent.putExtras(bundle);
        startActivityForResult(intent, TRANSTER_DEVICE_CODE);
    }

    @Override
    public void onDeleteClick(int index) {
        showRelieveUserDialog(adapter.getDataList().get(index).getId() + "");
    }

    @Override
    public void setPresenter(ManageDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void getManageInfoSuccess(ManageInfo resp) {
        mLoadingView.showContent();
        if (resp != null) {
            if (resp.getDevice_users() != null) {
                adapter.setDataList(resp.getDevice_users());
                String uid = resp.getDevice_users().get(0).getId() + "";
                if (uid.equals(userId)) {
                    isAdmin = true;
                    findViewById(R.id.right_button).setVisibility(View.VISIBLE);
                    releaseButton.setVisibility(View.GONE);
                    transferButton.setVisibility(View.GONE);
                } else {
                    isAdmin = false;
                    findViewById(R.id.right_button).setVisibility(View.GONE);
                    releaseButton.setVisibility(View.VISIBLE);
                    transferButton.setVisibility(View.GONE);
                }
            }
            String deviceId = "设备ID : " + "<font color='#000000'>" + resp.getDevice_code() + "</font>";
            deviceIdTv.setText(Html.fromHtml(deviceId));
            String deviceName = "设备昵称 : " + "<font color='#000000'>" + resp.getDevice_name() + "</font>";
            deviceNameTv.setText(Html.fromHtml(deviceName));
            Picasso.with(this).load(resp.getDevice_img_url()).into(deviceImg);
        }
    }

    @Override
    public void getManageInfoFailed() {
        mLoadingView.showError();
    }

    @Override
    public void releaseDeviceSuccess() {
        ToastUtils.showShort(this, "解除设备成功");
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void releaseUserSuccess() {
        ToastUtils.showShort(this, "解除用户成功");
        mPresenter.getManageInfo(userId, deviceId);
    }

    @Override
    public void transferDeviceSuccess() {
        ToastUtils.showShort(this, "转移管理员权限成功");
        mPresenter.getManageInfo(userId, deviceId);
    }
}
