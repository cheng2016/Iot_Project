package com.cds.iot.module.review;

import android.content.Context;

import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Util;

import chuangyuan.ycj.videolibrary.listener.DataSourceListener;
import okhttp3.OkHttpClient;


/**
 * Created by yangc on 2017/8/31.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:  自定义数数据源 工厂类
 */

public class Data2Source implements DataSourceListener {
    public static final String TAG = "OfficeDataSource";

    private Context context;

    public Data2Source(Context context) {
        this.context = context;
    }

    @Override
    public com.google.android.exoplayer2.upstream.DataSource.Factory getDataSourceFactory() {
         OkHttpClient okHttpClient = new OkHttpClient();
         OkHttpDataSourceFactory OkHttpDataSourceFactory=    new OkHttpDataSourceFactory(okHttpClient, Util.getUserAgent(context, context.getApplicationContext().getPackageName()),new DefaultBandwidthMeter() );
        //使用OkHttpClient 数据源工厂
           return  OkHttpDataSourceFactory;
        //默认数据源工厂
//        return new DefaultHttpDataSourceFactory(context.getPackageName(),null ,DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
//                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,true);
    }
}
