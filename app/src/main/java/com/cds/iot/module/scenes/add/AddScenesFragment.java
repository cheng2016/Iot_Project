package com.cds.iot.module.scenes.add;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.cds.iot.App;
import com.cds.iot.R;
import com.cds.iot.base.BaseFragment;
import com.cds.iot.data.entity.Device;
import com.cds.iot.data.entity.DeviceType;
import com.cds.iot.data.entity.SceneInfo;
import com.cds.iot.data.entity.SceneReq;
import com.cds.iot.data.entity.Scenes;
import com.cds.iot.module.adapter.ScenesDeviceTypeAdapter;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.ToastUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddScenesFragment extends BaseFragment implements View.OnClickListener, AddScenesContract.View {
    @Bind(R.id.scenes_name_edit)
    EditText scenesNameEdit;
    @Bind(R.id.scene_img)
    ImageView sceneImg;
    @Bind(R.id.device_type_listview)
    ListView deviceTypeListview;
    @Bind(R.id.insert_layout)
    RelativeLayout insertLayout;

    AddScenesPresenter mPresenter;
    ScenesDeviceTypeAdapter mAdapter;
    Scenes mScenes;

    boolean hidden = false;

    public static AddScenesFragment newInstance(Scenes scenes) {
        AddScenesFragment fragment = new AddScenesFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("scenes", scenes);
        fragment.setArguments(bundle);
        new AddScenesPresenter(fragment);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_add_scenes;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        insertLayout.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        new AddScenesPresenter(this);
        mAdapter = new ScenesDeviceTypeAdapter(getActivity());
        deviceTypeListview.setAdapter(mAdapter);
        mScenes = (Scenes) getArguments().getSerializable("scenes");
    }

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
        mAdapter.setDataList(mScenes.getDevice_types());
        Picasso.with(getActivity()).load(mScenes.getImg_url())
                .placeholder(R.mipmap.stage_loading)
                .error(R.mipmap.stage_loading).into(sceneImg);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.insert_layout:
                if (TextUtils.isEmpty(scenesNameEdit.getText().toString())) {
                    ToastUtils.showShort(App.getInstance(), "场景名不能为空");
                } else {
                    SceneReq req = new SceneReq();
                    req.setScene_id(mScenes.getId());
                    req.setScene_name(scenesNameEdit.getText().toString());
                    req.setUser_id(Integer.parseInt(PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID, "")));
                    List<Device> devices = new ArrayList<>();
                    for (DeviceType type : mScenes.getDevice_types()) {
                        if (type.getDevices() != null) {
                            for (Device d : type.getDevices()) {
                                devices.add(d);
                            }
                        }
                    }
                    req.setDevices(devices);
                    mPresenter.addSceneInfo(req);
                    insertLayout.setClickable(false);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void getSceneInfoSuccess(SceneInfo sceneInfo) {
    }

    @Override
    public void addSceneInfoSuccess() {
        ToastUtils.showShort(App.getInstance(), "场景添加成功！");
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void addSceneInfoFailed() {
        insertLayout.setClickable(true);
    }

    @Override
    public void getScenesImgSuccess(String url) {
        Picasso.with(getActivity()).load(url)
                .placeholder(R.mipmap.stage_loading)
                .error(R.mipmap.stage_loading).into(sceneImg);
    }

    public void onEventMainThread(Object event) {
        super.onEventMainThread(event);
        if (event instanceof Device && !hidden) {
            Device device = (Device) event;
            List<String> ids = new ArrayList<>();
            for (DeviceType deviceType : mScenes.getDevice_types()) {
                if (device.getType_id().equals(deviceType.getId())) {
                    if (device.getTag().equals("add")) {
                        device.setTag(null);
                        if (deviceType.getDevices() != null) {
                            deviceType.getDevices().add(device);
                        } else {
                            List<Device> devices = new ArrayList<>();
                            devices.add(device);
                            deviceType.setDevices(devices);
                        }
                    }
                }

                if (deviceType.getDevices() != null && !deviceType.getDevices().isEmpty()) {
                    for (Device d : deviceType.getDevices()) {
                        ids.add(deviceType.getId());
                    }
                }
            }
            mAdapter.setDataList(mScenes.getDevice_types());
            mPresenter.getScenesImg(mScenes.getId(), ids);
        }
    }

    @Override
    public void setPresenter(AddScenesContract.Presenter presenter) {
        mPresenter = (AddScenesPresenter) presenter;
    }
}