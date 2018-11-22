package com.cds.iot.module.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cds.iot.App;
import com.cds.iot.R;
import com.cds.iot.base.BaseFragment;
import com.cds.iot.data.Constant;
import com.cds.iot.data.entity.BindScenes;
import com.cds.iot.data.entity.Device;
import com.cds.iot.data.entity.DeviceListResp;
import com.cds.iot.data.entity.DeviceType;
import com.cds.iot.data.entity.SceneInfo;
import com.cds.iot.data.entity.WetherInfo;
import com.cds.iot.module.adapter.DeviceAdapter;
import com.cds.iot.module.adapter.MenuAdapter;
import com.cds.iot.module.adddevice.AddDeviceActivity;
import com.cds.iot.module.device.landline.LandlineActivity;
import com.cds.iot.module.device.mirror.RearviewMirrorActivity;
import com.cds.iot.module.device.watch.WatchActivity;
import com.cds.iot.module.fence.FenceActivity;
import com.cds.iot.module.manage.ManageBindActivity;
import com.cds.iot.module.message.user.UserApplyForActivity;
import com.cds.iot.module.scenes.ScenesEditActivity;
import com.cds.iot.module.scenes.add.AddScenesActivity;
import com.cds.iot.util.Logger;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.ToastUtils;
import com.cds.iot.view.CustomDialog;
import com.cds.iot.view.RefuseDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class DeviceFragment extends BaseFragment implements DeviceContract.View, View.OnClickListener, AdapterView.OnItemClickListener {
    public static final int REQUEST_ADD_DEVICE = 0X11;
    public static final int REQUEST_EDIT_DEVICE = 0X12;
    public static final int REQUEST_ADD_SCENES = 0X13;
    public static final int REQUEST_EDIT_SCENES = 0X14;

    @Bind(R.id.menu_listview)
    ListView menuListview;
    @Bind(R.id.device_gridview)
    GridView deviceGridview;
    @Bind(R.id.device_title)
    TextView deviceTitle;
    @Bind(R.id.edit_img)
    ImageView editImg;
    @Bind(R.id.bind_manage)
    TextView bindManageTv;
    @Bind(R.id.wether_title)
    TextView wetherTitle;
    @Bind(R.id.wether_detail)
    TextView wetherDetail;
    @Bind(R.id.location_tv)
    TextView locationTv;
    @Bind(R.id.add_scenes_img)
    ImageView addScenesImg;
    @Bind(R.id.emptyview)
    View emptyview;

    DeviceContract.Presenter mPresenter;
    /**
     * 常用按钮菜单
     */
    private View oftenView;

    MenuAdapter menuAdapter;
    DeviceAdapter deviceAdapter;

    SceneInfo mSceneInfo;
    List<BindScenes> mScenesList = new ArrayList<>();
    List<Device> mDeviceList = new ArrayList<>();


    List<Device> allDeviceList = new ArrayList<>();

    private String userId;

    public static DeviceFragment newInstance() {
        DeviceFragment mainFragment = new DeviceFragment();
        return mainFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_device;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        locationTv.setOnClickListener(this);
        view.findViewById(R.id.add_img).setOnClickListener(this);
        view.findViewById(R.id.often_layout).setOnClickListener(this);
        view.findViewById(R.id.edit_img).setOnClickListener(this);
        view.findViewById(R.id.bind_manage).setOnClickListener(this);
        oftenView = view.findViewById(R.id.often_layout);
        oftenView.setSelected(true);
        addScenesImg.setOnClickListener(this);
        menuListview.setOnItemClickListener(this);
        deviceGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Device device = deviceAdapter.getDataList().get(i);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("deviceId", device.getId());
                bundle.putString("deviceName", device.getName());
                intent.putExtras(bundle);
                int state = Integer.parseInt(device.getState());
                switch (state) {
                    case Constant.DEVICE_STATE_WAIT_AGREE:
                        intent.setClass(getActivity(), UserApplyForActivity.class);
                        intent.putExtra("uid", PreferenceUtils.getPrefString(getActivity(), PreferenceConstants.USER_ID, ""));
                        intent.putExtra("deviceId", device.getId());
                        intent.putExtra("msgType", device.getType_id());
                        intent.putExtra("deviceName", device.getName());
                        intent.putExtra("isAdmin", device.getIs_admin());
                        startActivityForResult(intent, REQUEST_EDIT_DEVICE);
                        break;
                    case Constant.DEVICE_STATE_REFUSE:
                        new RefuseDialog(getActivity())
                                .builder()
                                .setTitle("添加申请被拒绝")
                                .setMessage(device.getName() +"添加申请被管理员拒绝。")
                                .setReapplyButton(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mPresenter.reapplyDevice(device.getCode(),device.getName());
                                    }
                                })
                                .setReleaseButton(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mPresenter.releaseDevice(String.valueOf(userId), device.getId());
                                    }
                                }).show();
                        break;
                    case Constant.DEVICE_STATE_REVOKE:
                        new CustomDialog(getActivity())
                                .setTitle("设备被管理员解除")
                                .setMessage("是否移除该设备")
                                .setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mPresenter.releaseDevice(String.valueOf(userId), device.getId());
                                    }
                                }).show();
                        break;
                    case Constant.DEVICE_STATE_INSERT_SUCCESS:
                        int type = Integer.parseInt(device.getType_id());
                        switch (type) {
                            case Constant.DEVICE_TYPE_CAR_ANALYZER:
                                intent.setClass(getActivity(), FenceActivity.class);
                                startActivity(intent);
                                break;
                            case Constant.DEVICE_TYPE_TELEPHONE:
                                intent.setClass(getActivity(), LandlineActivity.class);
                                startActivityForResult(intent,Activity.RESULT_FIRST_USER);
                                break;
                            case Constant.DEVICE_TYPE_CAR_MIRROR:
                                intent.setClass(getActivity(), RearviewMirrorActivity.class);
                                startActivityForResult(intent, Activity.RESULT_FIRST_USER);
                                break;
                            case Constant.DEVICE_TYPE_WATCH:
                                intent.setClass(getActivity(), WatchActivity.class);
                                startActivity(intent);
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void initData() {
        new DevicePresenter(this);
        menuAdapter = new MenuAdapter(getActivity());
        menuListview.setAdapter(menuAdapter);
        deviceAdapter = new DeviceAdapter(getActivity());
        deviceGridview.setAdapter(deviceAdapter);
        mPresenter.getLocation(getActivity());
        mPresenter.getWetherInfo(getActivity(), "深圳");
        if (TextUtils.isEmpty(PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID, ""))) {
            Logger.e(TAG, "userid can not be null");
            return;
        }
        userId = PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID, "");
    }

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
        mPresenter.getDeviceList(userId);
    }

    public void queryDeviceList(){
        mPresenter.getDeviceList(userId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(DeviceContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.add_img:
                intent = new Intent().setClass(getActivity(), AddDeviceActivity.class);
                startActivityForResult(intent, REQUEST_ADD_DEVICE);
                break;
            case R.id.often_layout:
                //清除之前控件的选中状态
                menuAdapter.setSelectItem(-1);
                menuAdapter.notifyDataSetChanged();

                //更新点击控件的选中状态
                oftenView.setSelected(true);
                deviceTitle.setText("所有设备");
                editImg.setVisibility(View.GONE);
                if (allDeviceList == null || allDeviceList.size() == 0) {
                    bindManageTv.setVisibility(View.GONE);
                } else {
                    bindManageTv.setVisibility(View.VISIBLE);
                }
                deviceAdapter.setDataList(allDeviceList);
                break;
            case R.id.edit_img:
                intent = new Intent().setClass(getActivity(), ScenesEditActivity.class);
                if (mSceneInfo != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("SceneInfo", mSceneInfo);
                    bundle.putInt("index", menuAdapter.getSelectItem());
                    intent.putExtras(bundle);
                }
                startActivityForResult(intent, REQUEST_EDIT_SCENES);
                break;
            case R.id.bind_manage:
                intent = new Intent().setClass(getActivity(), ManageBindActivity.class);
                startActivityForResult(intent, Activity.RESULT_FIRST_USER);
                break;
            case R.id.add_scenes_img:
                intent = new Intent().setClass(getActivity(), AddScenesActivity.class);
                startActivityForResult(intent, REQUEST_ADD_SCENES);
                break;
            case R.id.location_tv:

                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
        editImg.setVisibility(View.VISIBLE);
        bindManageTv.setVisibility(View.GONE);
        //设置状态
        oftenView.setSelected(false);
        menuAdapter.setSelectItem(index);
        menuAdapter.notifyDataSetInvalidated();
        deviceTitle.setText(mScenesList.get(index).getName());
        mDeviceList.clear();
        BindScenes bindScenes = mScenesList.get(index);
        for (DeviceType deviceType : bindScenes.getDevice_types()) {
            if (deviceType.getDevices() != null && !deviceType.getDevices().isEmpty()) {
                for (Device device : deviceType.getDevices()) {
                    device.setMain_img_url(deviceType.getIcon_url());
                    device.setScene_id(bindScenes.getUser_scene_id());
                    device.setType_id(deviceType.getId());
                    mDeviceList.add(device);
                }
            }
        }
        deviceAdapter.setDataList(mDeviceList);
    }

    @Override
    public void getWetherInfoSuccess(WetherInfo wetherInfo) {
        if (wetherInfo.getLives() == null || wetherInfo.getLives().isEmpty()) {
            return;
        }
        WetherInfo.LivesBean live = wetherInfo.getLives().get(0);
        String title = live.getWeather() + "&nbsp;&nbsp;&nbsp;" + "<font color=\"#000000\"> " + live.getTemperature() + "℃" + "</font>";
        String detail = "风向：<font color=\"#000000\">" + live.getWinddirection() + "风 "
                + live.getWindpower() + "级 </font>" + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + "环境湿度：<font color=\"#000000\">" + live.getHumidity() + "% </font>";
        if (Build.VERSION.SDK_INT >= 24) {
            wetherTitle.setText(Html.fromHtml(title, Html.FROM_HTML_MODE_COMPACT));
            wetherDetail.setText(Html.fromHtml(detail, Html.FROM_HTML_MODE_COMPACT));
        } else {
            wetherTitle.setText(Html.fromHtml(title));
            wetherDetail.setText(Html.fromHtml(detail));
        }
    }

    @Override
    public void getLocationSuccess(String city) {
        mPresenter.getWetherInfo(getActivity(), city);
        locationTv.setText(city);
    }


    @Override
    public void getSceneInfoSuccess(SceneInfo sceneInfo) {
        mSceneInfo = sceneInfo;
        if (sceneInfo == null || sceneInfo.getBind_scenes() == null || sceneInfo.getBind_scenes().size() == 0) {
            menuAdapter.setSelectItem(-1);
            mScenesList.clear();
            menuAdapter.setDataList(mScenesList);


            deviceTitle.setText("所有设备");
            oftenView.setSelected(true);
            editImg.setVisibility(View.GONE);
            deviceAdapter.setDataList(allDeviceList);
            if (allDeviceList == null || allDeviceList.size() <= 0) {
                bindManageTv.setVisibility(View.GONE);
                emptyview.setVisibility(View.VISIBLE);
            } else {
                bindManageTv.setVisibility(View.VISIBLE);
                emptyview.setVisibility(View.GONE);
            }
            return;
        }
        emptyview.setVisibility(View.GONE);
        mScenesList = sceneInfo.getBind_scenes();
        menuAdapter.setDataList(mScenesList);
        mDeviceList.clear();
        BindScenes bindScenes;
        if (-1 == menuAdapter.getSelectItem()) {
            bindScenes = sceneInfo.getBind_scenes().get(0);
        } else {
            if (menuAdapter.getSelectItem() >= mScenesList.size()) {
                menuAdapter.setSelectItem(mScenesList.size() - 1);
            }
            bindScenes = sceneInfo.getBind_scenes().get(menuAdapter.getSelectItem());
        }
        if (bindScenes.getDevice_types() != null && !bindScenes.getDevice_types().isEmpty()) {
            for (DeviceType deviceType : bindScenes.getDevice_types()) {
                if (deviceType.getDevices() != null && !deviceType.getDevices().isEmpty()) {
                    for (Device device : deviceType.getDevices()) {
                        device.setMain_img_url(deviceType.getIcon_url());
                        device.setScene_id(bindScenes.getUser_scene_id());
                        device.setType_id(deviceType.getId());
                        mDeviceList.add(device);
                    }
                }
            }
        }

        if (menuAdapter.getSelectItem() != -1 && menuAdapter.getSelectItem() < mScenesList.size() && mScenesList.size() > 0) {
            deviceTitle.setText(menuAdapter.getDataList().get(menuAdapter.getSelectItem()).getName());
            menuAdapter.notifyDataSetChanged();
            oftenView.setSelected(false);
            editImg.setVisibility(View.VISIBLE);
            bindManageTv.setVisibility(View.GONE);
            deviceAdapter.setDataList(mDeviceList);
        } else {
            menuAdapter.setSelectItem(-1);
            deviceTitle.setText("所有设备");
            oftenView.setSelected(true);
            editImg.setVisibility(View.GONE);
            if (allDeviceList == null || allDeviceList.size() == 0) {
                bindManageTv.setVisibility(View.GONE);
            } else {
                bindManageTv.setVisibility(View.VISIBLE);
            }
            deviceAdapter.setDataList(allDeviceList);
        }
    }

    @Override
    public void getDeviceListSuccess(List<DeviceListResp> resp) {
        if (resp == null || resp.size() == 0) {
            bindManageTv.setVisibility(View.GONE);
            return;
        }
        allDeviceList.clear();
        for (DeviceListResp deviceListResp : resp) {
            for (Device device : deviceListResp.getDevices()) {
                device.setType_id(deviceListResp.getId() + "");
                allDeviceList.add(device);
            }
        }
        mPresenter.getSceneInfo(userId, Constant.SCENES_USER);
    }

    @Override
    public void releaseDeviceSuccess() {
        ToastUtils.showShort(getActivity(), "设备移除成功!");
        mPresenter.getDeviceList(userId);
    }

    @Override
    public void reapplyDeviceSuccess() {
        ToastUtils.showShort(getActivity(), "添加设备申请提交成功!");
        mPresenter.getDeviceList(userId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            mPresenter.getDeviceList(userId);
            switch (requestCode) {
                case REQUEST_ADD_DEVICE:
                    break;
                case REQUEST_EDIT_DEVICE:
                    break;
                case REQUEST_ADD_SCENES:
                    break;
                case REQUEST_EDIT_SCENES:
                    break;
                default:
                    break;
            }
        }
    }
}
