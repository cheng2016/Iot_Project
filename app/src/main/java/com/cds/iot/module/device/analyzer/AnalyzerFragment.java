package com.cds.iot.module.device.analyzer;

import android.os.Bundle;
import android.view.View;

import com.cds.iot.R;
import com.cds.iot.base.BaseFragment;

/**
 * @author Chengzj
 * @date 2018/10/10 11:25
 *
 * 分析仪
 */
public class AnalyzerFragment extends BaseFragment {

    public static AnalyzerFragment newInstance() {
        return new AnalyzerFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_analyzer;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

    }

    @Override
    protected void initData() {

    }
}
