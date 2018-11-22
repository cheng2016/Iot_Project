package com.cds.iot.module.device.mirror.video;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.cds.iot.App;
import com.cds.iot.data.BaseResp;
import com.cds.iot.data.entity.VideoEntity;
import com.cds.iot.data.source.remote.AObserver;
import com.cds.iot.data.source.remote.BaseObserver;
import com.cds.iot.data.source.remote.FileResponseBody;
import com.cds.iot.data.source.remote.HttpFactory;
import com.cds.iot.data.source.remote.ProgressListener;
import com.cds.iot.data.source.remote.ProgressResponseBody;
import com.cds.iot.data.source.remote.RetrofitCallback;
import com.cds.iot.data.source.remote.WifiApi;
import com.cds.iot.util.DeviceUtils;
import com.cds.iot.util.Logger;
import com.cds.iot.util.OkHttpUtils;
import com.cds.iot.util.ResourceUtils;
import com.cds.iot.util.ToastUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/31 17:24
 */
public class MirrorVideoPresenter implements MirrorVideoContract.Presenter {
    public final static String TAG = "MirrorVideoPresenter";
    private MirrorVideoContract.View view;
    private WifiApi mWifiApi;
    private CompositeDisposable mCompositeDisposable;

    public MirrorVideoPresenter(MirrorVideoContract.View view) {
        this.view = view;
        view.setPresenter(this);
        mCompositeDisposable = new CompositeDisposable();
        mWifiApi = HttpFactory.createSimpleRetrofit2(WifiApi.class, ResourceUtils.getProperties(App.getInstance(), "wifiUrl"));
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }


    @Override
    public void getVideo(int type, int offset, int limit) {
        mWifiApi.getVideoInfo(type, DeviceUtils.getDeviceIMEI(App.getInstance()), offset, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp<List<VideoEntity>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp<List<VideoEntity>> response) {
                        if ("200".equals(response.getInfo().getCode())) {
                            view.getVideoSuccess(response.getData());
                        } else {
                            view.getVideoFailed();
                            ToastUtils.showShort(App.getInstance(), response.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.getVideoFailed();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void getImage(int offset, int limit) {
        mWifiApi.getImageInfo(DeviceUtils.getDeviceIMEI(App.getInstance()), offset, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResp<List<VideoEntity>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(BaseResp<List<VideoEntity>> response) {
                        if ("200".equals(response.getInfo().getCode())) {
                            view.getVideoSuccess(response.getData());
                        } else {
                            view.getVideoFailed();
                            ToastUtils.showShort(App.getInstance(), response.getInfo().getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.getVideoFailed();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void downloadFileWithDynamicUrlAsync(TextView textView, int index, String url) {
        String fileName = url.split("/")[url.split("/").length - 1];
        String foldPath;
        if (fileName.endsWith(".mp4")) {
            foldPath = App.getInstance().getAppCacheDir() + "mirror/video/";
        } else {
            foldPath = App.getInstance().getAppCacheDir() + "mirror/image/";
        }
        String filePath = foldPath + fileName;
        Logger.i(TAG, "downloadFileWithDynamicUrlAsync  filePath：" + filePath + "，fileName：" + fileName);
        getRetrofitService(index, textView)
                .downloadFileWithDynamicUrlAsync(url.substring(32))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new BaseObserver<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        InputStream in = null;
                        FileOutputStream out = null;
                        byte[] buf = new byte[2048 * 10];
                        int len;
                        File file = null;
                        try {
                            File dir = new File(foldPath);
                            if (!dir.exists()) {// 如果文件不存在新建一个
                                dir.mkdirs();
                            }
                            in = response.byteStream();
                            file = new File(dir, fileName);
                            out = new FileOutputStream(file);
                            while ((len = in.read(buf)) != -1) {
                                out.write(buf, 0, len);
                            }
                            in.close();
                            out.close();
                        } catch (Exception e) {
                            Logger.i(TAG, "downloadFileWithDynamicUrlAsync ", e);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                view.getVideoFailed();
                            }
                        });
                    }

                    @Override
                    public void onComplete() {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                view.downloadSuccess(index, textView, filePath);
                            }
                        });
                    }
                });
    }

    private <T> WifiApi getRetrofitService(int index, TextView textView) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Response response = chain.proceed(chain.request());
                //将ResponseBody转换成我们需要的FileResponseBody
                return response.newBuilder()
                        .body(new ProgressResponseBody(response.body(), new ProgressListener() {
                            @Override
                            public void onProgress(long progress, long total, boolean done) {
                                Logger.i(TAG, String.format("进度： %d", (int) (progress * 100 / total)));
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        view.updateProgress(index, textView, (int) (progress * 100 / total));
                                    }
                                });
                            }
                        }))
                        .build();
            }
        });
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ResourceUtils.getProperties(App.getInstance(), "wifiUrl"))
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        WifiApi service = retrofit.create(WifiApi.class);
        return service;
    }

    @Override
    public void downloadFile(TextView textView, int index, String url) {
        String fileName = url.split("/")[url.split("/").length - 1];
        String filePath = App.getInstance().getAppCacheDir() + fileName;

        Logger.i(TAG, "downloadFile  filePath：" + filePath + "，fileName：" + fileName);

        OkHttpUtils.get().download(url, App.getInstance().getAppCacheDir(), fileName, new OkHttpUtils.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                Logger.i(TAG, "onDownloadSuccess");
                view.downloadSuccess(index, textView, filePath);
            }

            @Override
            public void onDownloading(int progress) {
                Logger.i(TAG, "currentBytes==" + progress);
                view.updateProgress(index, textView, progress);
            }

            @Override
            public void onDownloadFailed(Exception e) {
                Logger.i(TAG, "onDownloadFailed", e);
                view.downloadFailed(index, textView);
            }
        });
    }


    public static void main(String[] args) {
        String url = "http://192.168.43.1:8080/mirror/get/video/1/SMALL_20181107_112856.mp4";

        System.out.println("filename：" + url.split("/")[url.split("/").length - 1]);

        System.out.println("path：" + url.substring(32));


        System.out.println(String.format("%d", 32) + " %");
    }
}
