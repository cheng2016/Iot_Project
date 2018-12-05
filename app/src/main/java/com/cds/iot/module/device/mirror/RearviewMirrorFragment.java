package com.cds.iot.module.device.mirror;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.cds.iot.App;
import com.cds.iot.R;
import com.cds.iot.base.BaseFragment;
import com.cds.iot.data.entity.CarPosition;
import com.cds.iot.data.entity.WxPickupResp;
import com.cds.iot.module.device.mirror.deviceinfo.DeviceInfoActivity;
import com.cds.iot.module.device.mirror.navigation.NavigationActivity;
import com.cds.iot.module.device.mirror.track.TrackActivity;
import com.cds.iot.module.device.mirror.video.MirrorVideoActivity;
import com.cds.iot.util.Logger;
import com.cds.iot.util.MapUtils;
import com.cds.iot.util.ToastUtils;
import com.cds.iot.view.ActionSheetDialog;
import com.cds.iot.view.CustomDialog;
import com.cds.iot.view.MyAlertDialog;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author Chengzj
 * @date 2018/10/10 10:30
 * <p>
 * 后视镜界面
 */
public class RearviewMirrorFragment extends BaseFragment implements RearviewMirrorContract.View, AMap.InfoWindowAdapter, AMap.OnMarkerClickListener {
    @Bind(R.id.car_location)
    AppCompatImageView carLocationBtn;
    @Bind(R.id.location)
    AppCompatImageView locationBtn;
    @Bind(R.id.menu_detail)
    LinearLayout menuDetailLayout;
    @Bind(R.id.more)
    AppCompatImageView moreImg;

    private MapView mMapView = null;
    private AMap aMap;

    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    private AMapLocationClientOption mLocationOption = null;
    //定位成功的location定向
    private AMapLocation aMapLocation = null;
    //第一次成功定位标识
    private boolean isFirstLoc = true;

    private String deviceId = "";

    private String deviceName = "";

    private String isAdmin;

    private RearviewMirrorContract.Presenter mPresenter;

    public static RearviewMirrorFragment newInstance() {
        return new RearviewMirrorFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_rearview_mirror;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        //获取地图控件引用
        mMapView = (MapView) view.findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);//设置放大缩小图标是否显示
        uiSettings.setMyLocationButtonEnabled(false);// 定位按钮是否显示
        uiSettings.setCompassEnabled(true);//指南针是否显示
        uiSettings.setScaleControlsEnabled(false);//控制比例尺控件是否显示
        view.findViewById(R.id.wechat_access).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mPresenter.wxPickup(deviceId);
                return false;
            }
        });
        // 设置自定义InfoWindow样式
        aMap.setInfoWindowAdapter(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                mOnLocationChangedListener = onLocationChangedListener;
            }

            @Override
            public void deactivate() {
                mOnLocationChangedListener = null;
            }
        });
    }

    LocationSource.OnLocationChangedListener mOnLocationChangedListener;

    @Override
    protected void initData() {
        new RearviewMirrorPresenter(this);
        if (getArguments() != null) {
            deviceId = getArguments().getString("deviceId");
            deviceName = getArguments().getString("deviceName");
            isAdmin = getArguments().getString("isAdmin");
            mPresenter.getPosition(deviceId);
        }
        //初始化定位
        initLocation();
        mLocationClient.startLocation();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
            mLocationClient = null;
            mLocationOption = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            getActivity().setResult(Activity.RESULT_OK);
            if (data != null) {
                deviceName = data.getStringExtra("deviceName");
            }
        }
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        //初始化client
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(getActivity().getApplicationContext());
        }
        if (mLocationOption == null) {
            mLocationOption = getDefaultOption();
        }
        //设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        // 设置定位监听
        mLocationClient.setLocationListener(mLocationListener);
        //蓝点初始化
        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        //自定义蓝点bitmap样式
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.drawable.gps_point)));
        myLocationStyle.interval(2000);//设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);//只定位一次
        myLocationStyle.showMyLocation(true);//是否显示蓝点，只定位
        myLocationStyle.radiusFillColor(0x00000000);//隐藏精度范围显示
        myLocationStyle.strokeColor(0x00000000);//隐藏精度范围显示
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30 * 1000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true，这里设置不适用缓存定位
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    //声明定位回调监听器
    private final AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                //点击定位按钮 能够将地图的中心移动到定位点
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    mOnLocationChangedListener.onLocationChanged(location);

                    Logger.d(TAG, "onLocationChanged success，location ：" + new Gson().toJson(location));
                    aMapLocation = location;
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    if (isFirstLoc) {
                        isFirstLoc = false;
                    } else {
                        CameraUpdate cameraupdate = CameraUpdateFactory.newLatLng(latLng);
                        aMap.moveCamera(cameraupdate);
                    }
                } else {
                    Logger.e(TAG, "onLocationChanged error，location ：" + new Gson().toJson(location));
                }
            }
        }
    };

    CustomDialog mCustomDialog;


    @OnClick({R.id.car_location, R.id.location, R.id.more, R.id.remote_recording, R.id.remote_photography, R.id.driving_video, R.id.driving_track, R.id.wechat_access, R.id.booking_navigation, R.id.device})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        intent.putExtra("deviceName", deviceName);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("isAdmin", isAdmin);
        switch (view.getId()) {
            case R.id.car_location:
                mPresenter.getPosition(deviceId);
                break;
            case R.id.location:
//                mLocationClient.setLocationOption(mLocationOption);
                mLocationClient.startLocation();
                break;
            case R.id.more:
                if ("show".equals(moreImg.getTag())) {
                    menuDetailLayout.setVisibility(View.INVISIBLE);
                    moreImg.setImageResource(R.mipmap.btn_more_unfold);
                    moreImg.setTag("gone");
                } else {
                    menuDetailLayout.setVisibility(View.VISIBLE);
                    moreImg.setImageResource(R.mipmap.btn_more_fold);
                    moreImg.setTag("show");
                }
                break;
            case R.id.remote_recording:
                showConfirmDialog("远程摄像", "是否确认发送命令？", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.getRemoteVideotape(deviceId);
                    }
                });
                break;
            case R.id.remote_photography:
                showConfirmDialog("远程拍照", "是否确认发送命令？", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.getRemotePhotograph(deviceId);
                    }
                });
                break;
            case R.id.driving_video:
                intent.setClass(getActivity(), MirrorVideoActivity.class);
                startActivity(intent);
                break;
            case R.id.driving_track:
                intent.setClass(getActivity(), TrackActivity.class);
                startActivity(intent);
                break;
            case R.id.wechat_access:
                mPresenter.wxPickup(deviceId);
                break;
            case R.id.booking_navigation:
                intent.setClass(getActivity(), NavigationActivity.class);
                startActivity(intent);
                break;
            case R.id.device:
                intent.setClass(getActivity(), DeviceInfoActivity.class);
                startActivityForResult(intent, Activity.RESULT_FIRST_USER);
                break;
        }
    }

    void showConfirmDialog(String title, String message, View.OnClickListener listener){
        if (mCustomDialog == null) {
            mCustomDialog = new CustomDialog(getActivity())
                    .setTitle(title)
                    .setMessage(message);
        }
        if (!mCustomDialog.isShowing()) {
            mCustomDialog.setPositiveButton("确认", getResources().getColor(R.color.theme_color),listener);
            mCustomDialog.show();
        }
    }

    @Override
    public void getPositionSuccess(CarPosition resp) {
        if (!TextUtils.isEmpty(resp.getLatitude()) && !TextUtils.isEmpty(resp.getLongitude())) {
            LatLng latLng = new LatLng(Double.parseDouble(resp.getLatitude()), Double.parseDouble(resp.getLongitude()));
            CameraUpdate cameraupdate = CameraUpdateFactory.newLatLng(latLng);
            aMap.moveCamera(cameraupdate);
            aMap.moveCamera(CameraUpdateFactory.zoomTo(aMap.getMaxZoomLevel() - 5));

            MarkerOptions markerOption = new MarkerOptions()
                    .title(resp.getPlace_memo())
                    .snippet(resp.getDevice_time())
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(), R.mipmap.icn_car_map)))
                    .draggable(true)
                    .setFlat(true);//设置marker平贴地图效果
            Marker marker = aMap.addMarker(markerOption);
            marker.showInfoWindow();
        } else {
            ToastUtils.showShort(App.getInstance(), "未获取到车辆位置");
        }
    }

    @Override
    public void getRemotePhotographSuccess() {
        ToastUtils.showShort(App.getInstance(), "远程拍照指令发送成功！");
    }

    @Override
    public void getRemotePhotographFail() {
        ToastUtils.showShort(App.getInstance(), "远程拍照指令发送成功！");
    }

    @Override
    public void getRemoteVideotapeSuccess() {
        ToastUtils.showShort(App.getInstance(), "远程摄像指令发送成功！");
    }

    @Override
    public void getRemoteVideotapeFail() {
        ToastUtils.showShort(App.getInstance(), "远程摄像指令发送失败，请重试");
    }

    @Override
    public void wxPickupSuccess(final WxPickupResp resp) {
        new MyAlertDialog(getActivity())
                .setTitle("即将离开微信？")
                .setMessage("发送本消息给微信好友，并让好友点击进入页面发送位置。智能后视镜收到好友位置将自动规划导航路线。", Gravity.LEFT)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPresenter.shareWebPageNetwork(getActivity(), resp);
                    }
                }).showDialog();
    }

    @Override
    public void setPresenter(RearviewMirrorContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (TextUtils.isEmpty(marker.getTitle())) {
            return true;
        }
        marker.showInfoWindow();
        return false;
    }

    View infoWindow = null;

    @Override
    public View getInfoWindow(Marker marker) {
        if (infoWindow == null) {
            infoWindow = LayoutInflater.from(getActivity()).inflate(
                    R.layout.dialog_map_car_infowindow, null);
        }
        render(marker, infoWindow);
        return infoWindow;
    }

    /**
     * 自定义infowinfow窗口
     */
    public void render(final Marker marker, View view) {
        TextView contentTv = view.findViewById(R.id.content);
        TextView timeTv = view.findViewById(R.id.time);
        contentTv.setText(marker.getTitle());
        timeTv.setText(marker.getSnippet());
        view.findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marker.hideInfoWindow();
                new ActionSheetDialog(getActivity())
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem("高德导航", ActionSheetDialog.SheetItemColor.Blue,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        if (aMapLocation == null) {
                                            ToastUtils.showShort(App.getInstance(), "未获取到当前位置经纬度，请重新定位！");
                                            return;
                                        }
                                        Logger.i(TAG, "openGaoDeNavi：" + new Gson().toJson(aMapLocation));
                                        MapUtils.openGaoDeNavi(getActivity(), aMapLocation.getLatitude(), aMapLocation.getLongitude(), aMapLocation.getAddress(), marker.getPosition().latitude, marker.getPosition().longitude, marker.getTitle());
                                    }
                                }).show();
            }
        });
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
