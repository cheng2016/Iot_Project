package com.cds.iot.module.fence;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.data.entity.FenceInfo;
import com.cds.iot.module.adapter.FenceAdapter;
import com.cds.iot.module.fence.edit.FenceEditActivity;

import java.util.List;

import butterknife.Bind;

/**
 * @author chengzj
 * @date 2018/9/5 13:51
 * <p>
 * 电子围栏界面
 */
public class FenceActivity extends BaseActivity implements FenceContract.View, View.OnClickListener, AdapterView.OnItemClickListener, FenceAdapter.OnCheckBoxClickListener {
    @Bind(R.id.listview)
    ListView listview;

    FenceAdapter adapter;

    FenceContract.Presenter mPresenter;

    private String deviceId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_electric_fence;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((TextView) findViewById(R.id.title)).setText("电子围栏");
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.right_button).setVisibility(View.VISIBLE);
        findViewById(R.id.right_button).setOnClickListener(this);
        AppCompatImageView rightImg = (AppCompatImageView) findViewById(R.id.right_img);
        rightImg.setVisibility(View.VISIBLE);
        rightImg.setImageResource(R.mipmap.btn_addfence);
        listview.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        new FencePresenter(this);
        adapter = new FenceAdapter(this);
        adapter.setListener(this);
        listview.setAdapter(adapter);

        if (getIntent() != null && getIntent().getExtras() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            mPresenter.getFenceInfo(deviceId);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(FenceContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.right_button:
                Intent intent = new Intent();
                intent.setClass(FenceActivity.this, FenceEditActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void getFenceInfoSuccess(List<FenceInfo> resp) {
        adapter.setDataList(resp);
    }

    @Override
    public void updateFenceInfoSuccess() {

    }

    @Override
    public void getFenceInfoFail() {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent().setClass(FenceActivity.this, FenceEditActivity.class);
        Bundle bundle = new Bundle();
        FenceInfo fenceInfo = adapter.getDataList().get(i);
        fenceInfo.setDevice_id(deviceId);
        bundle.putSerializable("fenceInfo", fenceInfo);
        intent.putExtras(bundle);
        startActivityForResult(intent, RESULT_FIRST_USER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mPresenter.getFenceInfo(deviceId);
        }
    }

    @Override
    public void onOptionClick(int index, boolean isCheck) {
        FenceInfo info = adapter.getDataList().get(index);
        info.setDevice_id(deviceId);
        if (isCheck) {
            info.setEnable("0");
        } else {
            info.setEnable("1");
        }
        mPresenter.updateFenceInfo(info);
    }
}
