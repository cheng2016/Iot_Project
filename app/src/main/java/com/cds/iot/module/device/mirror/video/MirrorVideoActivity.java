package com.cds.iot.module.device.mirror.video;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;

import butterknife.Bind;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/31 17:17
 * <p>
 * 行车录像界面
 */
public class MirrorVideoActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.vp_horizontal_ntb)
    ViewPager viewPager;
    @Bind(R.id.tab)
    TabLayout tabLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mirror_video;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((TextView) findViewById(R.id.title)).setText("行车记录");
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            Fragment[] mFragments = new Fragment[3];
            String[] titles = new String[]{"常规视频","锁定视频","照片"};
            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public Fragment getItem(int i) {
                switch (i) {
                    case 0:
                        if(mFragments[0] == null){
                            mFragments[0] = MirrorVideoFragment.newInstance(0);
                        }
                        return mFragments[0];
                    case 1:
                        if(mFragments[1] == null){
                            mFragments[1] = MirrorVideoFragment.newInstance(1);
                        }
                        return mFragments[1];
                    case 2:
                        if(mFragments[2] == null){
                            mFragments[2] = MirrorVideoFragment.newInstance(2);
                        }
                        return mFragments[2];
                    default:
                        return null;
                }
            }

            /**
             * //此方法用来显示tab上的名字
             * @param position
             * @return
             */
            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        });
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
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
