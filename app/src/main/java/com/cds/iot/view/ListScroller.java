package com.cds.iot.view;

import android.content.Context;
import android.widget.AbsListView;

import com.squareup.picasso.Picasso;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/11/16 14:14
 *
 * Picasso 与Listview 可见时加载 快速滑动时候停止加载
 */
public class ListScroller implements AbsListView.OnScrollListener {
    Context context;

    public ListScroller(Context context) {
        this.context = context;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        final Picasso picasso = Picasso.with(context);
        if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
            //如果在暂停或者触摸的情况下完成重置
            picasso.resumeTag(context);
        } else {
            //停止更新
            picasso.pauseTag(context);
        }
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }


}