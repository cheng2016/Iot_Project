package com.cds.iot.module.device.mirror.video;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cds.iot.App;
import com.cds.iot.R;
import com.cds.iot.base.BaseFragment;
import com.cds.iot.data.entity.VideoEntity;
import com.cds.iot.module.adapter.VideoAdapter;
import com.cds.iot.module.review.ExoPlayActivity;
import com.cds.iot.module.review.ImagePagerActivity;
import com.cds.iot.module.review.YjPlayerActvity;
import com.cds.iot.util.DimenUtils;
import com.cds.iot.util.Logger;
import com.cds.iot.util.ToastUtils;
import com.cheng.refresh.library.PullToRefreshBase;
import com.cheng.refresh.library.PullToRefreshGridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/31 18:42
 */
public class MirrorVideoFragment extends BaseFragment implements MirrorVideoContract.View, PullToRefreshBase.OnRefreshListener<GridView>, View.OnClickListener, VideoAdapter.OnDownloadClickListener {
    @Bind(R.id.error_layout)
    RelativeLayout errorLayout;
    @Bind(R.id.description_img)
    AppCompatImageView descriptionImg;
    @Bind(R.id.refresh_btn)
    AppCompatButton refreshBtn;
    @Bind(R.id.refresh_listView)
    PullToRefreshGridView refreshListView;

    @Bind(R.id.empty_layout)
    LinearLayout emptyLayout;
    @Bind(R.id.empty_img)
    AppCompatImageView emptyImg;
    @Bind(R.id.empty_tv)
    TextView emptyTv;

    private GridView mGridView;

    private VideoAdapter adapter;

    private MirrorVideoContract.Presenter mPresenter;

    public static final int REQUEST_NUM = 20;
    private int offset = 0;
    private boolean hasMoreData = false;//是否有更多数据
    private boolean isLoadMore = false;//是否加载更多
    List<VideoEntity> mDataList = new ArrayList<>();

    private int type = -1;

    public static MirrorVideoFragment newInstance(int index) {
        MirrorVideoFragment mainFragment = new MirrorVideoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", index);
        mainFragment.setArguments(bundle);
        return mainFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mirror_video;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        refreshBtn.setOnClickListener(this);
        refreshListView.setPullLoadEnabled(false);//上拉加载是否可用
        refreshListView.setScrollLoadEnabled(true);//判断滑动到底部加载是否可用
        refreshListView.setPullRefreshEnabled(true);//设置是否能下拉
        refreshListView.setOnRefreshListener(this);
        mGridView = refreshListView.getRefreshableView();
        mGridView.setNumColumns(2);
        int margin = DimenUtils.dp2px(App.getInstance(), 10);
        mGridView.setHorizontalSpacing(margin);
        mGridView.setVerticalSpacing(margin);
        adapter = new VideoAdapter(getActivity());
        adapter.seOnDownloadtListener(this);
        mGridView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        new MirrorVideoPresenter(this);
        type = getArguments().getInt("type");
        if (type == 2) {
            emptyImg.setImageResource(R.mipmap.icn_empty_picture);
            emptyTv.setText("当前没有图片");
        } else {
            emptyImg.setImageResource(R.mipmap.icn_empty_video);
            emptyTv.setText("当前没有视频");
        }
        errorLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
        getData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
        ButterKnife.unbind(this);
    }

    private void getData() {
        if (type == 2) {
            mPresenter.getImage(offset, REQUEST_NUM);
        } else {
            mPresenter.getVideo(type, offset, REQUEST_NUM);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<GridView> pullToRefreshBase) {
        offset = 0;
        isLoadMore = false;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<GridView> pullToRefreshBase) {
        offset++;
        isLoadMore = true;
        getData();
    }

    @Override
    public void getVideoSuccess(List<VideoEntity> list) {
        hideProgressDilog();
        errorLayout.setVisibility(View.GONE);
        refreshListView.setVisibility(View.VISIBLE);
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
        ((Animatable) descriptionImg.getDrawable()).stop();
    }

    @Override
    public void getVideoFailed() {
        hideProgressDilog();
        errorLayout.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.GONE);
        refreshListView.setVisibility(View.GONE);
        ((Animatable) descriptionImg.getDrawable()).start();
    }

    @Override
    public void updateProgress(int index, TextView textView, int progress) {
        adapter.getDataList().get(index).setProgress(progress);
        textView.setText(String.format("%d", progress) + "%");
    }

    @Override
    public void downloadSuccess(int index, TextView textView, String path) {
        ToastUtils.showShort(App.getInstance(), "文件已保存到：" + path);
        adapter.getDataList().get(index).setProgress(100);
        if (path.endsWith(".mp4")) {
            adapter.getDataList().get(index).setVideoPath(path);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void downloadFailed(int positionInAdapter, TextView textView) {
        ToastUtils.showLong(getContext(), "下载失败");
        adapter.getDataList().get(positionInAdapter).setProgress(-1);
        adapter.notifyDataSetChanged();
    }

    TextView getProgressView(int positionInAdapter) {
        TextView progressView = null;
        if (positionInAdapter >= mGridView.getFirstVisiblePosition() && positionInAdapter <= mGridView.getLastVisiblePosition()) {
            int positionInListView = positionInAdapter - mGridView.getFirstVisiblePosition();
            progressView = (TextView) mGridView.getChildAt(positionInListView)
                    .findViewById(R.id.download_img);
        }
        return progressView;
    }

    @Override
    public void setPresenter(MirrorVideoContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onClick(View v) {
        if (R.id.refresh_btn == v.getId()) {
            offset = 0;
            isLoadMore = false;
            getData();
            showProgressDilog();
        }
    }

    @Override
    public void onDownloadClick(TextView textView, int index, String path) {
        Logger.i(TAG, "onDownloadClick：" + index);
        mPresenter.downloadFileWithDynamicUrlAsync(textView, index, path);
    }

    @Override
    public void onPlayClick(String name, String videoPath) {
        Intent intent = new Intent();
        if (videoPath.startsWith("http")) {
            intent.setClass(getActivity(), YjPlayerActvity.class);
        } else {
            intent.setClass(getActivity(), ExoPlayActivity.class);
        }
        intent.putExtra("url", videoPath);
        intent.putExtra("title", name);
        startActivity(intent);
    }

    @Override
    public void onImageClick(String imagePath) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), ImagePagerActivity.class);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, new String[]{imagePath});
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 0);
        startActivity(intent);
    }
}
