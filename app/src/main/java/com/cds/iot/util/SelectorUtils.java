package com.cds.iot.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;


/**
 * Selector生成状态选择器
 */
public class SelectorUtils {
    /**
     * 用java代码的方式动态生成状态选择器
     */
    public static Drawable generatePressedSelector(Drawable pressed, Drawable normal) {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_pressed}, pressed);//  状态  , 设置按下的图片
        drawable.addState(new int[]{android.R.attr.state_checked}, pressed);//  状态  , 设置选中的图片
        drawable.addState(new int[]{android.R.attr.state_selected}, pressed);//  状态  ,设置挑选的图片
        drawable.addState(new int[]{}, normal);//默认状态,默认状态下的图片
        //根据SDK版本设置状态选择器过度动画/渐变选择器/渐变动画
//        if (Build.VERSION.SDK_INT > 10) {
//            drawable.setEnterFadeDuration(500);
//            drawable.setExitFadeDuration(500);
//        }
        return drawable;
    }

    /**
     * 从 drawable 获取图片 id 给 Imageview 添加 selector
     *  @param context 调用方法的 Activity
     * @param idNormal 默认图片的 id
     * @param idPress  点击图片的 id
     * @param iv   点击的 view
     */
    public static void addSelectorFromDrawable(Context context , int idNormal, int idPress,ImageView iv){

        StateListDrawable drawable = new StateListDrawable();
        Drawable normal = context.getResources().getDrawable(idNormal);
        Drawable press = context.getResources().getDrawable(idPress);
        drawable.addState(new int[]{android.R.attr.state_pressed},press);
        drawable.addState(new int[]{-android.R.attr.state_pressed},normal);
        iv.setBackgroundDrawable(drawable);
    }

    /**
     * 从 drawable 获取图片 id 给 Button 添加 selector
     * @param context 调用方法的 Activity
     * @param idNormal 默认图片的 id
     * @param idPress  点击图片的 id
     * @param button   点击的 view
     */

    public static void addSelectorFromDrawable(Context context , int idNormal, int idPress, Button button){

        StateListDrawable drawable = new StateListDrawable();
        Drawable normal = context.getResources().getDrawable(idNormal);
        Drawable press = context.getResources().getDrawable(idPress);
        drawable.addState(new int[]{android.R.attr.state_pressed},press);
        drawable.addState(new int[]{-android.R.attr.state_pressed},normal);
        button.setBackgroundDrawable(drawable);
    }

    /**
     * 从网络获取图片 给 ImageView 设置 selector
     *  @param clazz 调用方法的类
     * @param normalUrl 获取默认图片的链接
     * @param pressUrl 获取点击图片的链接
     * @param imageView 点击的 view
     */
    public static void addSeletorFromNet(final Class clazz, final String normalUrl, final String pressUrl, final ImageView imageView){
        new AsyncTask<Void,Void,Drawable>(){

            @Override
            protected Drawable doInBackground(Void... params) {
                StateListDrawable drawable = new StateListDrawable();
                Drawable normal = loadImageFromNet(clazz,normalUrl);
                Drawable press = loadImageFromNet(clazz, pressUrl);
                drawable.addState(new int[]{android.R.attr.state_pressed},press);
                drawable.addState(new int[]{-android.R.attr.state_pressed},normal);
                return drawable;
            }

            @Override
            protected void onPostExecute(Drawable drawable) {
                super.onPostExecute(drawable);
                imageView.setBackgroundDrawable(drawable);
            }
        }.execute();

    }

    /**
     *
     * 从网络获取图片 给 Button 设置 selector
     * @param clazz 调用方法的类
     * @param normalUrl 获取默认图片的链接
     * @param pressUrl 获取点击图片的链接
     * @param button 点击的 view
     */
    public static void addSeletorFromNet(final Class clazz, final String normalUrl, final String pressUrl, final Button button){
        new AsyncTask<Void,Void,Drawable>(){

            @Override
            protected Drawable doInBackground(Void... params) {
                StateListDrawable drawable = new StateListDrawable();
                Drawable normal = loadImageFromNet(clazz,normalUrl);
                Drawable press = loadImageFromNet(clazz, pressUrl);
                drawable.addState(new int[]{android.R.attr.state_pressed},press);
                drawable.addState(new int[]{-android.R.attr.state_pressed},normal);
                return drawable;
            }

            @Override
            protected void onPostExecute(Drawable drawable) {
                super.onPostExecute(drawable);
                button.setBackgroundDrawable(drawable);
            }
        }.execute();

    }

    /**
     * 从网络获取图片
     * @param clazz 调用方法的类
     * @param netUrl 获取图片的链接
     * @return  返回一个 drawable 类型的图片
     */
    private static Drawable loadImageFromNet(Class clazz, String netUrl) {
        Drawable drawable =null;
        try {
            drawable = Drawable.createFromStream(new URL(netUrl).openStream(), "netUrl.jpg");
        } catch (IOException e) {
            Logger.e(clazz.getName(),e.getMessage());
        }

        return drawable;
    }
}
