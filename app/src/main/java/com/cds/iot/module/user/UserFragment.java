package com.cds.iot.module.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cds.iot.App;
import com.cds.iot.R;
import com.cds.iot.base.BaseFragment;
import com.cds.iot.data.entity.UserInfo;
import com.cds.iot.module.about.AboutActivity;
import com.cds.iot.module.feedback.FeedBackActivity;
import com.cds.iot.module.web.WebActivity;
import com.cds.iot.module.review.ImagePagerActivity;
import com.cds.iot.module.setting.SettingActivity;
import com.cds.iot.module.user.detail.UserDetailActivity;
import com.cds.iot.module.wxpublic.WxPublicNumblerActivity;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.picasso.PicassoCircleTransform;
import com.squareup.picasso.Picasso;

import butterknife.Bind;

public class UserFragment extends BaseFragment implements UserContract.View, View.OnClickListener {
    public final static int REQUEST_USER_INFO = 0x01;

    @Bind(R.id.nickname)
    TextView nickname;
    @Bind(R.id.head_img)
    ImageView headImg;

    private UserPresenter mPresenter;

    private UserInfo mUserInfoResp;

    public static UserFragment newInstance() {
        UserFragment mainFragment = new UserFragment();
        return mainFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        view.findViewById(R.id.setting_layout).setOnClickListener(this);
        view.findViewById(R.id.product_description_layout).setOnClickListener(this);
        view.findViewById(R.id.about_us_layout).setOnClickListener(this);
        view.findViewById(R.id.feedback_layout).setOnClickListener(this);
        view.findViewById(R.id.wx_public_number_layout).setOnClickListener(this);
        view.findViewById(R.id.user_detail_layout).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.title)).setText("我的");
        headImg.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        new UserPresenter(this);
    }

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
        String userId = PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID,"");
        if(!TextUtils.isEmpty(userId)){
            mPresenter.getUserInfo(Integer.parseInt(userId));
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.head_img:
                if (mUserInfoResp != null) {
                    intent = new Intent(getActivity(), ImagePagerActivity.class);
                    // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, new String[]{mUserInfoResp.getHead_img()});
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 0);
                    startActivity(intent);
                }
                break;
            case R.id.user_detail_layout:
                intent = new Intent().setClass(getActivity(), UserDetailActivity.class);
                if (mUserInfoResp != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("userInfo", mUserInfoResp);
                    intent.putExtras(bundle);
                }
                startActivityForResult(intent,REQUEST_USER_INFO);
                break;
            case R.id.product_description_layout:
                intent = new Intent().setClass(getActivity(), WebActivity.class);
                startActivity(intent);
                break;
            case R.id.about_us_layout:
                intent = new Intent().setClass(getActivity(), AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.feedback_layout:
                intent = new Intent().setClass(getActivity(), FeedBackActivity.class);
                startActivity(intent);
                break;
            case R.id.wx_public_number_layout:
                intent = new Intent().setClass(getActivity(), WxPublicNumblerActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_layout:
                intent = new Intent().setClass(getActivity(), SettingActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_USER_INFO && resultCode == Activity.RESULT_OK){
            int userId = Integer.parseInt(PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID,""));
            mPresenter.getUserInfo(userId);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(UserContract.Presenter presenter) {
        mPresenter = (UserPresenter) presenter;
    }

    @Override
    public void getUserInfoSuccess(UserInfo resp) {
        mUserInfoResp = resp;
        nickname.setText(resp.getNickname());
        if (TextUtils.isEmpty(resp.getHead_img())) {
            if (resp.getSex() == 0) {
                headImg.setImageResource(R.mipmap.btn_female_small);
            } else {
                headImg.setImageResource(R.mipmap.btn_male_small);
            }
        } else {
            Picasso.with(getActivity())
                    .load(resp.getHead_img())
                    .error(resp.getSex() == 0 ? R.mipmap.btn_female_small : R.mipmap.btn_male_small)
                    .transform(new PicassoCircleTransform())
                    .into(headImg);
        }
    }
}
