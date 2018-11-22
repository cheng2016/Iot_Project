package com.cds.iot.module.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cds.iot.data.entity.Scenes;
import com.cds.iot.module.scenes.add.AddScenesFragment;

import java.util.List;

public class ScenePagerAdapter extends FragmentPagerAdapter {

    private Fragment[] mFragments;

    private List<Scenes> scenesList;

    private ScenePagerAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new Fragment[3];
    }

    public ScenePagerAdapter(FragmentManager fm, Fragment[] fragments,List<Scenes> scenesList) {
        super(fm);
        this.mFragments = fragments;
        this.scenesList = scenesList;
    }

    @Override
    public Fragment getItem(int position) {
        if(mFragments[position] == null){
            mFragments[position] = AddScenesFragment.newInstance(scenesList.get(position));
        }
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }
}
