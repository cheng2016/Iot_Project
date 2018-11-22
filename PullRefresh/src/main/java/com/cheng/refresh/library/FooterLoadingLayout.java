package com.cheng.refresh.library;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



/**
 * 这个类封装了下拉刷新的布局
 * 
 * @author Li Hong
 * @since 2013-7-30
 */
public class FooterLoadingLayout extends LoadingLayout {
	/** 进度条 */
	private ImageView mProgressBar;
	/** 显示的文本 */
	private TextView mHintView;
	private LinearLayout mFooterContainer;
	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 */
	public FooterLoadingLayout(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 * @param attrs
	 *            attrs
	 */
	public FooterLoadingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 *            context
	 */
	private void init(Context context) {
		mProgressBar = (ImageView) findViewById(R.id.pull_to_load_footer_progressbar);
		mHintView = (TextView) findViewById(R.id.pull_to_load_footer_hint_textview);
		mFooterContainer=(LinearLayout)findViewById(R.id.pull_to_load_footer_content);
		setState(State.RESET);
	}

	@Override
	protected View createLoadingView(Context context, AttributeSet attrs) {
		View container = LayoutInflater.from(context).inflate(
				R.layout.pull_to_load_footer, null);
		return container;
	}

	@Override
	public void setLastUpdatedLabel(CharSequence label) {
	}

	@Override
	public int getContentSize() {
		if (null != mFooterContainer) {
			return mFooterContainer.getHeight();
		}

		return (int) (getResources().getDisplayMetrics().density * 40);
	}

	@Override
	protected void onStateChanged(State curState, State oldState) {
		mProgressBar.setVisibility(View.GONE);
		//imageView指的是需要播放动画的ImageView控件
		AnimationDrawable animationDrawable = (AnimationDrawable) mProgressBar.getBackground();
		//启动动画
		animationDrawable.stop();
		mHintView.setVisibility(View.GONE);
		super.onStateChanged(curState, oldState);
	}

	@Override
	protected void onReset() {
		mHintView.setText(R.string.pushmsg_center_load_more_text);
	}

	@Override
	protected void onPullToRefresh() {
		mHintView.setVisibility(View.VISIBLE);
		mHintView.setText(R.string.pull_to_refresh_header_hint_normal2);
	}

	@Override
	protected void onReleaseToRefresh() {
		mHintView.setVisibility(View.VISIBLE);
		mHintView.setText(R.string.pull_to_refresh_header_hint_ready);
	}

	@Override
	protected void onRefreshing() {
		mProgressBar.setVisibility(View.VISIBLE);
		//imageView指的是需要播放动画的ImageView控件
		AnimationDrawable animationDrawable = (AnimationDrawable) mProgressBar.getBackground();
		//启动动画
		animationDrawable.start();
		mHintView.setVisibility(View.VISIBLE);
		mHintView.setText(R.string.pushmsg_center_load_more_ongoing_text);
	}

	@Override
	protected void onNoMoreData() {
		mHintView.setVisibility(View.VISIBLE);
		mHintView.setText(R.string.pushmsg_center_no_more_msg);
	}
}
