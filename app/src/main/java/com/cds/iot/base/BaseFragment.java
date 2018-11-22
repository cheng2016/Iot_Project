package com.cds.iot.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cds.iot.util.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;

/**
 * Created by chengzj on 2017/6/17.
 *
 * 实现懒加载的基类fragment
 */

public abstract class BaseFragment extends Fragment {
    public String TAG = "";

    private ProgressDialog progressDialog;

    private Context context;
    /**
     * 标志位：fragment是否可见
     */
    private boolean isVisible;

    /**
     * 标志位：是否已加载fragment视图
     */
    private boolean isPrepared = false;
    /**
     * 标志位：是否已进行懒加载数据，保证数据只加载一次
     */
    private boolean isLoadData = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        TAG = this.getClass().getSimpleName();
        Logger.d(TAG, "setUserVisibleHint() -> isVisibleToUser: " + isVisibleToUser);
        if(getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Logger.d(TAG, "onAttach");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logger.d(TAG, "onCreateView");
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Logger.d(TAG, "onViewCreated");
        initView(view, savedInstanceState);
        isPrepared = true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Logger.d(TAG, "onActivityCreated");
        initData();
        onVisible();//防止viewpager加载的第一个视图时,第一个视图提前执行setUserVisibleHint方法导致懒加载无效的问题
    }

    /**
     * fragment可见时执行
     */
    protected void onVisible(){
        Logger.d(TAG, "onVisible  isPrepared：" + isPrepared + "，isVisible：" + isVisible + "，isLoadData：" + isLoadData);
        if (!isPrepared || !isVisible || isLoadData) {
            return;
        }
        isLoadData = true;
        onLazyLoad();
    }

    /**
     * 懒加载核心方法
     */
    protected  void onLazyLoad(){
        Logger.d(TAG,"onLazyLoad");
    }

    /**
     * fragment不可见时执行
     */
    protected void onInvisible(){}

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d(TAG, "onActivityResult");
    }

    @Subscribe
    public void onEventMainThread(Object event) {

    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.d(TAG, "onStart");
    }


    @Override
    public void onResume() {
        super.onResume();
        Logger.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.d(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.d(TAG, "onDestroyView");
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d(TAG, "onDestroy");
    }

    protected abstract int getLayoutId();

    protected abstract void initView(View view, Bundle savedInstanceState);

    protected abstract void initData();


    public void showProgressDilog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
        }
        progressDialog.setMessage("加载中...");
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void showProgressDilog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
        }
        progressDialog.setMessage(message);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void hideProgressDilog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
