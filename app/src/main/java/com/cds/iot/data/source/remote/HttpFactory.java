package com.cds.iot.data.source.remote;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.cds.iot.App;
import com.cds.iot.util.DeviceUtils;
import com.cds.iot.util.MD5Utils;
import com.cds.iot.util.NetUtils;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chengzj on 2017/6/18.
 */

public class HttpFactory {
    public static final String TAG = "HttpFactory";

    private HttpFactory() {
    }

    public static <T> T createRetrofit2(Class<T> service) {
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ResourceUtils.getProperties(App.getInstance(), "url"))
                .client(getCacheSSLOkHttpClient("kuda.com.cer"))
                .build();
        return retrofit.create(service);
    }

    public static <T> T createSimpleRetrofit2(Class<T> service) {
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(HttpApi.base_url)
                .client(getSimpleOkHttpClient())
                .build();
        return retrofit.create(service);
    }

    public static <T> T createSimpleRetrofit2(Class<T> service,String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .client(getSimpleOkHttpClient())
                .build();
        return retrofit.create(service);
    }

    public static <T> T createWxSSLService(final Class<T> service, Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(getSSLClient(context))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Https.wxBaseurl)
                .build();
        return retrofit.create(service);
    }

    public static <T> T createWetherSSLService(final Class<T> service, Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(getSSLClient(context, "amap.com.cer"))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(WHttps.wetherBaseurl)
                .build();
        return retrofit.create(service);
    }

    public static OkHttpClient getSimpleOkHttpClient() {
        return new OkHttpClient.Builder()
                .writeTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(20 * 1000, TimeUnit.MILLISECONDS)
                .connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                //设置拦截器，显示日志信息
                .addInterceptor(httpLoggingInterceptor)
                .build();
    }

    private static OkHttpClient getCacheOkHttpClient() {
        //设置缓存路径
        final File httpCacheDirectory = new File(App.getInstance().getCacheDir(), "okhttpCache");
        Log.i(TAG, httpCacheDirectory.getAbsolutePath());
        //设置缓存 10M
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);   //缓存可用大小为10M

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .writeTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(20 * 1000, TimeUnit.MILLISECONDS)
                .connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                //设置拦截器，显示日志信息
                .addInterceptor(httpLoggingInterceptor)
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .cache(cache)
                .build();
        return okHttpClient;
    }


    private static OkHttpClient getCacheSSLOkHttpClient(String fileName) {
        //设置缓存路径
        final File httpCacheDirectory = new File(App.getInstance().getCacheDir(), "okhttpCache");
        Log.i(TAG, httpCacheDirectory.getAbsolutePath());
        //设置缓存 10M
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);   //缓存可用大小为10M

        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .writeTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(20 * 1000, TimeUnit.MILLISECONDS)
                .connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                //设置拦截器，显示日志信息
                .addInterceptor(httpLoggingInterceptor)
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .cache(cache);
        HttpsUtils.SSLParams sslParams = null;
        try {
            sslParams = setInputStream(App.getInstance().getAssets().open(fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        return builder.build();
    }

    private static final HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String token = PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.ACCESS_TOKEN, "");
            if(TextUtils.isEmpty(token)){
                token = MD5Utils.MD5(System.currentTimeMillis() + DeviceUtils.getDeviceIMEI(App.getInstance()));
                PreferenceUtils.setPrefString(App.getInstance(), PreferenceConstants.ACCESS_TOKEN, token);
            }
            //统一请求头添加
            request = request.newBuilder()
                    .header("custom_token", token).build();
            //获取网络状态
            int netWorkState = NetUtils.getNetworkState(App.getInstance());
            //无网络，请求强制使用缓存
            if (netWorkState == NetUtils.NETWORK_NONE) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .header("custom_token", token)
                        .build();
            }

            Response originalResponse = chain.proceed(request);
            switch (netWorkState) {
                case NetUtils.NETWORK_MOBILE://moblie network 情况下缓存5s
                    int maxAge = 0;
                    return originalResponse.newBuilder()
                            .removeHeader("Pragma")
                            .removeHeader("Cache-Control")
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .build();
                case NetUtils.NETWORK_WIFI://wifi network 情况下不使用缓存
                    maxAge = 0;
                    return originalResponse.newBuilder()
                            .removeHeader("Pragma")
                            .removeHeader("Cache-Control")
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .build();

                case NetUtils.NETWORK_NONE://none network 情况下离线缓存4周
                    int maxStale = 60 * 60 * 24 * 4 * 7;
                    return originalResponse.newBuilder()
                            .removeHeader("Pragma")
                            .removeHeader("Cache-Control")
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .build();
                default:
                    throw new IllegalStateException("network state  is Erro!");
            }
        }
    };

    public static OkHttpClient getSSLClient(Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .writeTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(20 * 1000, TimeUnit.MILLISECONDS)
                .connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                .addInterceptor(httpLoggingInterceptor);
        HttpsUtils.SSLParams sslParams = null;
//      sslParams = setInputStream( new Buffer().writeUtf8(WEIXIN).inputStream());
        try {
            sslParams = setInputStream(context.getAssets().open("mp.weixin.qq.com.crt"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);

        return builder.build();
    }

    public static OkHttpClient getSSLClient(Context context, String fileName) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .writeTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(20 * 1000, TimeUnit.MILLISECONDS)
                .connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                .addInterceptor(httpLoggingInterceptor);
        HttpsUtils.SSLParams sslParams = null;
        try {
            sslParams = setInputStream(context.getAssets().open(fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }

        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);

        return builder.build();
    }

    private static HttpsUtils.SSLParams setInputStream(InputStream... inputStream) {
        return HttpsUtils.getSslSocketFactory(null, null, inputStream);
    }
}
