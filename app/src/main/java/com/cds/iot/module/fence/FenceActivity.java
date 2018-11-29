package com.cds.iot.module.fence;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.data.entity.FenceInfo;
import com.cds.iot.module.adapter.FenceAdapter;
import com.cds.iot.module.fence.edit.FenceEditActivity;
import com.cds.iot.util.ToastUtils;
import com.cheng.refresh.library.PullToRefreshBase;
import com.cheng.refresh.library.PullToRefreshListView;

import java.util.List;

import butterknife.Bind;

/**
 * @author chengzj
 * @date 2018/9/5 13:51
 * <p>
 * 电子围栏界面
 */
public class FenceActivity extends BaseActivity implements FenceContract.View, View.OnClickListener, AdapterView.OnItemClickListener, FenceAdapter.OnCheckBoxClickListener, PullToRefreshBase.OnRefreshListener<ListView> {
    @Bind(R.id.refresh_listView)
    PullToRefreshListView refreshListView;

    ListView mListView;

    FenceAdapter adapter;

    FenceContract.Presenter mPresenter;

    String deviceId;

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

        refreshListView.setPullLoadEnabled(false);//上拉加载是否可用
        refreshListView.setScrollLoadEnabled(false);//判断滑动到底部加载是否可用
        refreshListView.setPullRefreshEnabled(true);//设置是否能下拉
        refreshListView.setOnRefreshListener(this);
        mListView = refreshListView.getRefreshableView();
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        new FencePresenter(this);
        adapter = new FenceAdapter(this);
        adapter.setListener(this);
        mListView.setAdapter(adapter);
        if (getIntent() != null && getIntent().getExtras() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            mPresenter.getFenceList(deviceId);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
        if(!TextUtils.isEmpty(deviceId)){
            mPresenter.getFenceList(deviceId);
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
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
                intent.putExtra("deviceId",deviceId);
                intent.setClass(FenceActivity.this, FenceEditActivity.class);
                startActivityForResult(intent, RESULT_FIRST_USER);
                break;
            default:
                break;
        }
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

    @Override
    public void onDeleteClick(int index) {
        mPresenter.deleteFenceInfo(adapter.getDataList().get(index).getId());
    }

    @Override
    public void getFenceListSuccess(List<FenceInfo> resp) {
        refreshListView.onPullDownRefreshComplete();
        adapter.setDataList(resp);
    }


    @Override
    public void getFenceListFail() {
    }

    @Override
    public void updateFenceInfoSuccess() {
        ToastUtils.showShort(this,"修改电子围栏成功");
    }

    @Override
    public void deleteFenceInfoSuccess() {
        ToastUtils.showShort(this,"删除电子围栏成功");
        mPresenter.getFenceList(deviceId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mPresenter.getFenceList(deviceId);
        }
    }
}
