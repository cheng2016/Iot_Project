package com.cds.iot.module.device.watch;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;

/**
 * @author Chengzj
 * @date 2018/10/10 10:39
 *
 * 儿童手表
 */
public class WatchActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_watch;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((TextView) findViewById(R.id.title)).setText("儿童手表");
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        WatchFragment fragment = WatchFragment.newInstance();
        Bundle bundle = getIntent().getExtras();
        fragment.setArguments(bundle);
        ft.replace(R.id.content_container, fragment);
        ft.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            default:
                break;
        }
    }
}
