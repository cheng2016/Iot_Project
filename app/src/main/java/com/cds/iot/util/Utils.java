package com.cds.iot.util;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
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
    public static void calListViewWidthAndHeight(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        View listItem = null;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        //totalHeight += 150;
        int cur = 100;
        if(listAdapter.getCount()<10){
            cur = 60;
        }
        totalHeight += (totalHeight/cur)*(listAdapter.getCount());

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() *(listAdapter.getCount())); //(listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
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
