package com.cds.iot.module.device.landline.wireless;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.ImageView;

import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;

import butterknife.Bind;

/**
 * @author Chengzj
 * @date 2018/10/9 16:49
 *
 * 无线座机界面
 */
public class TelephoneActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.title)
    AppCompatTextView titleTv;
    @Bind(R.id.back_img)
    ImageView backImg;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_telephone;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleTv.setText("一连座机");
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        TelephoneFragment fragment = TelephoneFragment.newInstance();
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
