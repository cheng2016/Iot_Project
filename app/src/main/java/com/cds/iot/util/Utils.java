package com.cds.iot.util;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.util.Random;

/**
 * @author Chengzj
 * @date 2018/10/9 19:27
 */
public class Utils {

    /**
     * 判断列表第一个是否显示
     * @param listView
     * @return
     */
    public static boolean isFirstItemVisible(AbsListView listView) {
        Adapter adapter = listView.getAdapter();
        if (null != adapter && !adapter.isEmpty()) {
            int mostTop = listView.getChildCount() > 0 ? listView.getChildAt(0).getTop() : 0;
            return mostTop >= 0;
        } else {
            return true;
        }
    }

    /**
     * 设置listview高度
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10); // 可删除   listView.setLayoutParams(params); }
    }


    /**
     * 计算ListView宽高
     *
     * @param listView
     */
    public static int calListViewWidthAndHeight(ListView listView) {
        ListAdapter mAdapter = listView.getAdapter();
        if (mAdapter == null) {
            return 0;
        }
        int totalHeight = 0;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);
            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            //mView.measure(0, 0);
            totalHeight += mView.getMeasuredHeight();
            Log.d("数据" + i, String.valueOf(totalHeight));
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        Log.d("数据", "listview总高度="+ params.height);
        listView.setLayoutParams(params);
        listView.requestLayout();
        return totalHeight;
    }

    /**
     * 计算GridView宽高
     * @param gridView
     */
    public static void calGridViewWidthAndHeight(int numColumns ,GridView gridView) {

        // 获取GridView对应的Adapter
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, gridView);
            listItem.measure(0, 0); // 计算子项View 的宽高

            if ((i+1)%numColumns == 0) {
                totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
            }

            if ((i+1) == len && (i+1)%numColumns != 0) {
                totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
            }
        }

        totalHeight += 40;

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        gridView.setLayoutParams(params);
    }



    /**
     * 将bitmap转化为二进制
     * @param bmp
     * @param needRecycle
     * @return
     */
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main (String[] args){
        for(int i = 0;i<1000;i++){
            System.out.println(new Random().nextInt(1000));

            System.out.println(new Random().nextInt());
        }
    }

}
