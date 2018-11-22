package com.cds.iot.module.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cds.iot.module.device.DeviceFragment;
import com.cds.iot.module.message.MessageFragment;
import com.cds.iot.module.user.UserFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] mFragments = new Fragment[3];


    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public MainPagerAdapter(FragmentManager fm,Fragment[] fragments) {
        super(fm);
        this.mFragments = fragments;
    }

    public Fragment[] getmFragments() {
        return mFragments;
    }

    public void setmFragments(Fragment[] mFragments) {
        this.mFragments = mFragments;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if(mFragments[0] == null){
                    mFragments[0] = DeviceFragment.newInstance();
                }
                return mFragments[0];
            case 1:
                if(mFragments[1] == null){
                    mFragments[1] = MessageFragment.newInstance();
                }
                return mFragments[1];
            case 2:
                if(mFragments[2] == null){
                    mFragments[2] = UserFragment.newInstance();
                }
                return mFragments[2];
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }
}
