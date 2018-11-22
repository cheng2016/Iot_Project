package com.cds.iot.module.wxpublic;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.squareup.picasso.Picasso;

/**
 * 微信公众号
 */
public class WxPublicNumblerActivity extends BaseActivity implements View.OnClickListener {

    ImageView qrImg;

    String url = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=gQGD7zwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyb0JVbGdnTWNjZGsxajlQbzFyMW8AAgRJ4lZbAwSAUQEA";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_public_number;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        qrImg = (ImageView) findViewById(R.id.qr_img);
    }

    @Override
    protected void initData() {
        Picasso.with(this).load(url).into(qrImg);
        ((TextView) findViewById(R.id.title)).setText("微信公众号");
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
