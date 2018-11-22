package com.cds.iot.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author Chengzj
 * @date 2018/10/10 10:58
 *
 * 状态栏工具类
 *
 */
public class StatusBarUtils {
  /**
   * 改变状态栏颜色
   * @param activity
   * @param statusColor
   */
  public static void setWindowStatusBarColor(Activity activity, int statusColor) {
    //5.0以上使用该方法
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = activity.getWindow();
      //取消状态栏透明
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      //添加Flag把状态栏设为可绘制模式
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      //设置状态栏颜色
      window.setStatusBarColor(statusColor);
      //设置系统状态栏处于可见状态
      window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
      //让view不根据系统窗口来调整自己的布局
      ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
      View mChildView = mContentView.getChildAt(0);
      if (mChildView != null) {
        ViewCompat.setFitsSystemWindows(mChildView, false);
        ViewCompat.requestApplyInsets(mChildView);
      }
    }
  }

  /**
   * 获取状态栏高度
   * @param context
   * @return
   */
  public static int getStatusBarHeight(Context context){
    int result = 0;
    int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      result = context.getResources().getDimensionPixelSize(resourceId);
    }
    return result;
  }
}
