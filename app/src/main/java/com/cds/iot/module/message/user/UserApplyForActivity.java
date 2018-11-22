package com.cds.iot.module.message.user;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.data.Constant;
import com.cds.iot.data.entity.DeviceUser;
import com.cds.iot.data.entity.ManageInfo;
import com.cds.iot.module.adapter.UserHistoryAdapter;
import com.cds.iot.util.ToastUtils;
import com.cds.iot.util.picasso.PicassoCircleTransform;
import com.cds.iot.view.MyAlertDialog;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import ezy.ui.layout.LoadingLayout;

/**
 * @author Chengzj
 * @date 2018/9/17 10:40
 * <p>
 * 用户提交绑定申请界面
 */
public class UserApplyForActivity extends BaseActivity implements View.OnClickListener, UserApplyForContract.View {
    @Bind(R.id.head_img)
    AppCompatImageView headImg;
    @Bind(R.id.name_tv)
    AppCompatTextView nameTv;
    @Bind(R.id.phone_tv)
    AppCompatTextView phoneTv;
    @Bind(R.id.device_img)
    AppCompatImageView deviceImg;
    @Bind(R.id.device_id_tv)
    AppCompatTextView deviceIdTv;
    @Bind(R.id.device_name_tv)
    AppCompatTextView deviceNameTv;
    @Bind(R.id.list_view)
    ListView listView;
    @Bind(R.id.revoke)
    RelativeLayout revokeButton;

    UserApplyForContract.Presenter mPresenter;

    String userId;

    String deviceId;

    String msgType;

    UserHistoryAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message_user_apply_for;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((TextView) findViewById(R.id.title)).setText("设备添加");
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        revokeButton = (RelativeLayout) findViewById(R.id.revoke);
        revokeButton.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        new UserApplyForPresenter(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            userId = getIntent().getStringExtra("uid");
            deviceId = getIntent().getStringExtra("deviceId");
            msgType = getIntent().getStringExtra("msgType");
            adapter = new UserHistoryAdapter(this);
            listView.setAdapter(adapter);
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
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    void showRevokeDialog() {
        new MyAlertDialog(this)
                .setTitle("确认要撤销此申请？")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPresenter.revokeApply(userId, deviceId);
                    }
                }).showDialog();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.revoke:
                showRevokeDialog();
                break;
            default:
                break;
        }
    }

    @Override
    public void getManageInfoSuccess(ManageInfo resp) {
        mLoadingView.showContent();
        String deviceId = "设备ID : " + "<font color='#000000'>" + resp.getDevice_code() + "</font>";
        deviceIdTv.setText(Html.fromHtml(deviceId));
        String deviceName = "设备昵称 : " + "<font color='#000000'>" + resp.getDevice_name() + "</font>";
        deviceNameTv.setText(Html.fromHtml(deviceName));
        Picasso.with(this).load(resp.getDevice_img_url()).into(deviceImg);

        if (Constant.DEVICE_WAIT_AGREE.equals(resp.getState())) {
            revokeButton.setVisibility(View.VISIBLE);
        } else if (Constant.DEVICE_REVOKE.equals(resp.getState())) {
            revokeButton.setVisibility(View.GONE);
        } else {
            revokeButton.setVisibility(View.GONE);
        }
        adapter.setDataList(resp.getUser_bind_history());

        if (resp.getDevice_users() != null) {
            for (DeviceUser user : resp.getDevice_users()) {
                if (Constant.IS_ADMIN.equals(user.getIs_admin())) {
                    if (!TextUtils.isEmpty(user.getImg_url())) {
                        Picasso.with(this).load(user.getImg_url()).placeholder(R.mipmap.btn_male_big).error(R.mipmap.btn_male_big).transform(new PicassoCircleTransform()).into(headImg);
                    }
                    String name = user.getName() + "<font color='#3f3f3f'>" + "（管理员）" + "</font>";
                    nameTv.setText(Html.fromHtml(name));
                    phoneTv.setText(user.getPhone_number());
                    break;
                }
            }
        }
    }

    @Override
    public void getManageInfoFailed() {
        mLoadingView.showError();
    }

    @Override
    public void revokeApplySuccess() {
        ToastUtils.showShort(this, "撤销申请成功");
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void setPresenter(UserApplyForContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
