package com.cds.iot.module.message;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.base.BaseFragment;
import com.cds.iot.data.Constant;
import com.cds.iot.data.entity.MessageType;
import com.cds.iot.data.entity.SMessage;
import com.cds.iot.data.source.local.SMessageDaoUtils;
import com.cds.iot.module.adapter.MessageAdapter;
import com.cds.iot.module.message.admin.AdminApplyForActivity;
import com.cds.iot.module.message.user.UserApplyForActivity;
import com.cds.iot.module.review.ImagePagerActivity;
import com.cds.iot.module.review.YjPlayerActvity;
import com.cds.iot.util.Logger;
import com.cds.iot.view.MenuPopWindow;
import com.cheng.refresh.library.PullToRefreshBase;
import com.cheng.refresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class MessageFragment extends BaseFragment implements MessageContract.View, View.OnClickListener, MenuPopWindow.OnOptionClickListener, AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener<ListView>, MessageAdapter.OnMessageLisenter {
    public static final int REQUEST_NUM = 10;

    @Bind(R.id.empty_layout)
    RelativeLayout emptyLayout;
    @Bind(R.id.refresh_listView)
    PullToRefreshListView refreshListView;
    private ListView mListView;

    MessageAdapter adapter;

    MessageContract.Presenter mPresenter;

    private String deviceType = "";

    List<SMessage> mDataList = new ArrayList<>();

    private int offset = 0;
    private boolean hasMoreData = false;//是否有更多数据
    private boolean isLoadMore = false;//是否加载更多


    MenuPopWindow mMenuPopWindow;

    public static MessageFragment newInstance() {
        MessageFragment mainFragment = new MessageFragment();
        return mainFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_message;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        view.findViewById(R.id.right_button).setVisibility(View.VISIBLE);
        view.findViewById(R.id.right_button).setOnClickListener(this);
        view.findViewById(R.id.right_img).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.title)).setText("消息");
        view.findViewById(R.id.title).setOnClickListener(this);
        refreshListView.setPullLoadEnabled(false);//上拉加载是否可用
        refreshListView.setScrollLoadEnabled(true);//判断滑动到底部加载是否可用
        refreshListView.setPullRefreshEnabled(true);//设置是否能下拉
        refreshListView.setOnRefreshListener(this);
        mListView = refreshListView.getRefreshableView();
    }

    @Override
    protected void initData() {
        new MessagePresenter(this);
        adapter = new MessageAdapter(getActivity());
        adapter.setLisenter(this);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
        mPresenter.queryMessage(deviceType, offset);
        mPresenter.getMessageTypeList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//        refreshView.setLastUpdatedLabel(DateUtils.parseMillisToString(System.currentTimeMillis()));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                offset = 0;
                isLoadMore = false;
                mPresenter.queryMessage(deviceType, offset);

            }
        }, 1200);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                offset++;
                isLoadMore = true;
                mPresenter.queryMessage(deviceType, offset);
            }
        }, 1200);
    }

    @Override
    public void setPresenter(MessageContract.Presenter presenter) {
        mPresenter = presenter;
    }

    int count = 0;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.right_button:
                if (mMenuPopWindow == null) {
                    mMenuPopWindow = new MenuPopWindow(getActivity());
                }
                mMenuPopWindow.setSelect(select).showAtBottom(view).setListener(this);
                break;
            case R.id.title:
                count++;
                if (count > 7) {
                    SMessageDaoUtils daoUtils = new SMessageDaoUtils(getActivity());
                    daoUtils.deleteAll();
                    mPresenter.queryMessage(deviceType, offset);
                }
                break;
            default:
                break;
        }
    }

    int select = -1;

    @Override
    public void onOptionClick(int index, int typeId) {
        select = index;
        if (typeId == -1) {
            deviceType = "";
        } else {
            deviceType = String.valueOf(typeId);
        }
        offset = 0;
        isLoadMore = false;
        mPresenter.queryMessage(deviceType, offset);
    }

    @Override
    public void queryMessageSuccess(List<SMessage> list) {
        if (!isLoadMore) {
            mDataList.clear();
            if (list.isEmpty()) {
                emptyLayout.setVisibility(View.VISIBLE);
                refreshListView.setScrollLoadEnabled(false);
            } else {
                emptyLayout.setVisibility(View.GONE);
                refreshListView.setScrollLoadEnabled(true);
            }
        }
        if (list.size() == REQUEST_NUM) {
            hasMoreData = true;
        } else {
            hasMoreData = false;
        }
        mDataList.addAll(list);
        adapter.setDataList(mDataList);
        refreshListView.onPullDownRefreshComplete();
        refreshListView.onPullUpRefreshComplete();
        refreshListView.setHasMoreData(hasMoreData);
    }

    @Override
    public void queryMessage() {
        offset = 0;
        isLoadMore = false;
        mPresenter.queryMessage(deviceType, offset);
    }

    @Override
    public void getMessageTypeListSuccess(List<MessageType> dataList) {
        if (mMenuPopWindow == null) {
            mMenuPopWindow = new MenuPopWindow(getActivity());
        }
        mMenuPopWindow.setDataList(dataList);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Logger.i(TAG, "onItemClick：" + i);
        Intent intent = new Intent();
        intent.putExtra("uid", adapter.getDataList().get(i).getUid());
        intent.putExtra("deviceId", adapter.getDataList().get(i).getDeviceId());
        intent.putExtra("msgType", adapter.getDataList().get(i).getMsgType());
        switch (Integer.parseInt(adapter.getDataList().get(i).getMsgType())) {
            case Constant.MESSAGE_TYPE_USER_ADD_APPLY:
                intent.setClass(getActivity(), AdminApplyForActivity.class);
                startActivity(intent);
                break;
            case Constant.MESSAGE_TYPE_USER_ADD_APPLY_REFUSE:
            case Constant.MESSAGE_TYPE_USER_ADD_APPLY_AGREE:
            case Constant.MESSAGE_TYPE_ADMIN_REMOVE_USER_DEVICE:
                intent.setClass(getActivity(), UserApplyForActivity.class);
                startActivity(intent);
                break;
            case Constant.MESSAGE_TYPE_USER_REMOVE_DEVICE:
                break;
        }
    }

    @Override
    public void onImageClick(int index) {
        SMessage message = adapter.getDataList().get(index);
        Intent intent = new Intent(getActivity(), ImagePagerActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, new String[]{message.getPhotoUrl()});
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 0);
        getActivity().startActivity(intent);
    }

    @Override
    public void onPlayClick(int index) {
        SMessage message = adapter.getDataList().get(index);
        Intent intent = new Intent(getActivity(), YjPlayerActvity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra("url", message.getVideoUrl());
        intent.putExtra("title", "");
        getActivity().startActivity(intent);
    }
}
