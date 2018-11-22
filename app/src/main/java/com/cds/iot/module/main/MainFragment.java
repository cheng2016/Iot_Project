package com.cds.iot.module.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.base.BaseFragment;
import com.cds.iot.data.entity.NewsList;
import com.google.gson.Gson;

import butterknife.Bind;

/**
 * Created by chengzj on 2017/6/17.
 */

public class MainFragment extends BaseFragment implements MainContract.View {
    MainContract.Presenter mMainPresenter;
    @Bind(R.id.message)
    TextView mTextView;

    public static MainFragment newInstance() {
        MainFragment mainFragment = new MainFragment();
        new MainPresenter(mainFragment);
        return mainFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        mMainPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMainPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mMainPresenter = presenter;
    }

    @Override
    public void showNewList(NewsList newsList) {
        mTextView.setText(new Gson().toJson(newsList));
    }
}
