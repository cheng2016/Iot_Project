package com.cds.iot.module.web;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;

/**
 * 产品说明
 * <p>
 * 第三方公众web界面
 */
public class WebActivity extends BaseActivity implements View.OnClickListener {

    private TextView titleTv;
    private FrameLayout contentLayout;

    private WebView webView;

    private ProgressBar pbProgress;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_third_web;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        titleTv = (TextView) findViewById(R.id.title);
        contentLayout = (FrameLayout) findViewById(R.id.content_layout);
        pbProgress = (ProgressBar) findViewById(R.id.pb_progress);
        webView = new WebView(this);
        contentLayout.addView(webView, 0);

        WebSettings webSettings = webView.getSettings();
        webSettings.setDatabaseEnabled(true);
        final String dbPath = getApplicationContext().getDir("db", Context.MODE_PRIVATE).getPath();
        webSettings.setDatabasePath(dbPath);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAppCacheEnabled(true);
        final String cachePath = getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        webSettings.setAppCachePath(cachePath);
        webSettings.setAppCacheMaxSize(5 * 1024 * 1024);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
                    pbProgress.setVisibility(View.GONE);
                } else {
                    pbProgress.setVisibility(View.VISIBLE);
                    // 加载中
                    pbProgress.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        //点击后退按钮,让WebView后退一页(也可以覆写Activity的onKeyDown方法)
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {  //表示按返回键
                        webView.goBack();   //后退
                        //webview.goForward();//前进
                        return true;    //已处理
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void initData() {
        titleTv.setText("产品说明");
        if (getIntent().getExtras() != null) {
            String url = getIntent().getStringExtra("url");
            String title = getIntent().getStringExtra("title");
            if (!TextUtils.isEmpty(title)) {
                titleTv.setText(title);
            }
            if (!TextUtils.isEmpty(url)) {
                webView.loadUrl(url);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            contentLayout.removeView(webView);

            webView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            webView.getSettings().setJavaScriptEnabled(false);
            webView.clearHistory();
            webView.clearView();
            webView.removeAllViews();

            try {
                webView.destroy();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
        }
    }
}
