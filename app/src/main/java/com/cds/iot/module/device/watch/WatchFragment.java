package com.cds.iot.module.device.watch;

import android.os.Bundle;
import android.view.View;

import com.cds.iot.R;
import com.cds.iot.base.BaseFragment;

/**
 * @author Chengzj
 * @date 2018/10/10 11:27
 *
 * 儿童手表
 */
public class WatchFragment extends BaseFragment{

    public static WatchFragment newInstance() {
        return new WatchFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_watch;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

    }

    @Override
    protected void initData() {

    }
}
