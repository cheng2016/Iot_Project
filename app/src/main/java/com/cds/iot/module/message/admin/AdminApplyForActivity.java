package com.cds.iot.module.message.admin;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.data.Constant;
import com.cds.iot.data.entity.ManageInfo;
import com.cds.iot.module.adapter.ManageUserAdapter;
import com.cds.iot.module.adapter.UserHistoryAdapter;
import com.cds.iot.util.ToastUtils;
import com.cds.iot.util.Utils;
import com.cds.iot.util.picasso.PicassoCircleTransform;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import ezy.ui.layout.LoadingLayout;

/**
 * @author Chengzj
 * @date 2018/9/17 14:08
 * <p>
 * 管理员收到绑定申请界面
 */
public class AdminApplyForActivity extends BaseActivity implements View.OnClickListener, AdminApplyForContract.View {
    @Bind(R.id.name_tv)
    AppCompatTextView nameTv;
    @Bind(R.id.phone_tv)
    AppCompatTextView phoneTv;
    @Bind(R.id.device_id_tv)
    AppCompatTextView deviceIdTv;
    @Bind(R.id.device_name_tv)
    AppCompatTextView deviceNameTv;
    @Bind(R.id.head_img)
    ImageView headImg;
    @Bind(R.id.device_img)
    AppCompatImageView deviceImg;
    @Bind(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @Bind(R.id.agree)
    RelativeLayout confirmButton;
    @Bind(R.id.refuse)
    RelativeLayout cancelButton;
    @Bind(R.id.user_grid_view)
    GridView userGridView;
    @Bind(R.id.list_view)
    ListView listView;


    AdminApplyForContract.Presenter mPresenter;

    ManageUserAdapter userAdapter;

    UserHistoryAdapter userHistoryAdapter;

    String userId;

    String deviceId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message_admin_apply_for;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((TextView) findViewById(R.id.title)).setText("设备添加申请");
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        new AdminApplyForPresenter(this);
        userAdapter = new ManageUserAdapter(this);
        userGridView.setAdapter(userAdapter);
        userHistoryAdapter = new UserHistoryAdapter(this);
        listView.setAdapter(userHistoryAdapter);

//        nameTv.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.mipmap.btn_female_big), null, null);
        if (getIntent() != null && getIntent().getExtras() != null) {
            userId = getIntent().getStringExtra("uid");
            deviceId = getIntent().getStringExtra("deviceId");
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.agree:
                mPresenter.processMessage(userId, deviceId, Constant.MANAGE_AGREE);
                break;
            case R.id.refuse:
                mPresenter.processMessage(userId, deviceId, Constant.MANAGE_REFUSE);
                break;
            default:
                break;
        }
    }

    @Override
    public void setPresenter(AdminApplyForContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void getManageInfoSuccess(ManageInfo resp) {
        mLoadingView.showContent();
        if (resp != null) {
            userAdapter.setDataList(resp.getDevice_users());
            Utils.calGridViewWidthAndHeight(4, userGridView);
            userHistoryAdapter.setDataList(resp.getUser_bind_history());
            Utils.calListViewWidthAndHeight(listView);
            if (!TextUtils.isEmpty(resp.getUser_img_url())) {
                Picasso.with(this).load(resp.getUser_img_url()).placeholder(R.mipmap.btn_male_big).error(R.mipmap.btn_male_big).transform(new PicassoCircleTransform()).into(headImg);
            }
            String name = resp.getUser_name() + "<font color='#3f3f3f'>" + "（申请人）" + "</font>";
            nameTv.setText(Html.fromHtml(name));
            phoneTv.setText(resp.getUser_phone());
            String deviceId = "设备ID : " + "<font color='#000000'>" + resp.getDevice_code() + "</font>";
            deviceIdTv.setText(Html.fromHtml(deviceId));
            String deviceName = "设备昵称 : " + "<font color='#000000'>" + resp.getDevice_name() + "</font>";
            deviceNameTv.setText(Html.fromHtml(deviceName));
            Picasso.with(this).load(resp.getDevice_img_url()).into(deviceImg);

            if (Constant.DEVICE_WAIT_AGREE.equals(resp.getState())) {
                bottomLayout.setVisibility(View.VISIBLE);
                confirmButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
            } else {
                bottomLayout.setVisibility(View.GONE);
                confirmButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void getManageInfoFailed() {
        mLoadingView.showError();
    }

    @Override
    public void processMessageSuccess() {
        ToastUtils.showShort(this, "提交成功");
        mPresenter.getManageInfo(userId, deviceId);
    }
}