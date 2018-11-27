package com.cds.iot;

import android.os.Environment;
import android.text.TextUtils;

import com.blankj.utilcode.util.Utils;
import com.cds.iot.base.BaseApplication;
import com.cds.iot.util.DeviceUtils;
import com.cds.iot.util.Logger;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.ResourceUtils;
import com.cds.iot.util.ToastUtils;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;

//import com.squareup.leakcanary.LeakCanary;

//import com.squareup.leakcanary.LeakCanary;

/**
 * Created by chengzj on 2017/6/17.
 */

public class App extends BaseApplication {
    public static final  String TAG = "App";

    public String HOST = "sit.wecarelove.com";

    public int PORT = 85;

    private static App mInstance;

    private String imageCacheDir;

    private String appCacheDir;

    public static App getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initAppTool();
        if (ResourceUtils.getProperties(this, "os_is_debug").equals("1")) {
//            CrashHandler.getInstance().init(this);
        }
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        LeakCanary.install(this);
    }

    void initAppTool() {
        //初始化工具类
        Utils.init(this);
        ToastUtils.isShow = true;
        initPicasoConfig();
    }

    private void initPicasoConfig() {
        if (DeviceUtils.isSDCardEnable()) {
            appCacheDir = Environment.getExternalStorageDirectory() + "/wecare/v4/";
        } else {
            appCacheDir = getCacheDir().getAbsolutePath() + "/wecare/v4/";
        }
        File directory = new File(appCacheDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        PreferenceUtils.setPrefString(this,PreferenceConstants.APP_CACHE_DIR,appCacheDir);
        imageCacheDir = appCacheDir + "image/";
        File file = new File(imageCacheDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        PreferenceUtils.setPrefString(this,PreferenceConstants.APP_IMAGE_CACHE_DIR,imageCacheDir);
        //设置图片内存缓存大小为运行时内存的八分之一
        long l = Runtime.getRuntime().maxMemory();
        OkHttpClient client = new OkHttpClient();
        client.setCache(new Cache(file, l / 8));
        Picasso picasso = new Picasso.Builder(this)
                .memoryCache(new LruCache((int) (l / 8)))
                .downloader(new OkHttpDownloader(client))
//                .defaultBitmapConfig(Bitmap.Config.RGB_565)
                .loggingEnabled(false)//picasso log日志
                .build();
        Picasso.setSingletonInstance(picasso);
    }

    public String getAppCacheDir() {
        if(TextUtils.isEmpty(appCacheDir)){
            appCacheDir = PreferenceUtils.getPrefString(this,PreferenceConstants.APP_CACHE_DIR,"");
            Logger.i(TAG, "getAppCacheDir：" + appCacheDir);
        }
        File directory = new File(appCacheDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return appCacheDir;
    }

    public String getImageCacheDir() {
        if(TextUtils.isEmpty(imageCacheDir)){
            imageCacheDir = PreferenceUtils.getPrefString(this,PreferenceConstants.APP_IMAGE_CACHE_DIR,"");
            Logger.i(TAG, "getImageCacheDir：" + imageCacheDir);
        }
        File directory = new File(imageCacheDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return imageCacheDir;
    }

}
