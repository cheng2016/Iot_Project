package com.cds.iot.module.scenes.add;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cds.iot.App;
import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.data.Constant;
import com.cds.iot.data.entity.SceneInfo;
import com.cds.iot.data.entity.Scenes;
import com.cds.iot.module.adapter.pager.ScenePagerAdapter;
import com.cds.iot.util.DimenUtils;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;

import java.util.List;

import butterknife.Bind;

/**
 * 添加场景页面
 */
public class AddScenesActivity extends BaseActivity implements View.OnClickListener, AddScenesContract.View {
    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.radio_group)
    RadioGroup radioGroup;

    AddScenesContract.Presenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scenes_add;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        new AddScenesPresenter(this);
        ((TextView) findViewById(R.id.title)).setText("场景");
        String userId = PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID, "");
        mPresenter.getSceneInfo(userId, Constant.SCENES_DEFAULT);
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

    @Override
    public void setPresenter(AddScenesContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void getSceneInfoSuccess(SceneInfo senceInfo) {
        if (senceInfo != null) {
            List<Scenes> scenesList = senceInfo.getScenes();
            if(scenesList.size() > 1){
                for (int i = 0; i < scenesList.size(); i++) {
                    RadioButton radioButton = new RadioButton(this);
                    radioButton.setId(i);
                    radioButton.setBackground(null);
                    radioButton.setButtonDrawable(R.drawable.selector_circle);
                    RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(radioGroup.getLayoutParams());
                    layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    int margin = DimenUtils.dp2px(this,8);
                    layoutParams.setMargins(margin, margin, margin, margin);
                    radioGroup.addView(radioButton,layoutParams);
                    if (i == 0) {
                        radioButton.setChecked(true);
                    }
                }
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                        viewPager.setCurrentItem(checkedId);
                    }
                });
            }

            viewPager.setAdapter(new ScenePagerAdapter(getSupportFragmentManager(), new Fragment[scenesList.size()],scenesList));
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    radioGroup.check(i);
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });
        }
    }

    @Override
    public void addSceneInfoSuccess() {
    }

    @Override
    public void addSceneInfoFailed() {
    }

    @Override
    public void getScenesImgSuccess(String url) {
    }
}
