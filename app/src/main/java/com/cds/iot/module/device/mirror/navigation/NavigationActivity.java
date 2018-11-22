package com.cds.iot.module.device.mirror.navigation;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.blankj.utilcode.util.KeyboardUtils;
import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.module.adapter.AutoCompleteTextAdapter;
import com.cds.iot.util.Logger;
import com.cds.iot.util.MapUtils;
import com.cds.iot.util.ToastUtils;
import com.cds.iot.view.CustomDialog;
import com.google.gson.Gson;

import java.util.List;

import butterknife.Bind;

/**
 * @author Chengzj
 * @date 2018/10/10 15:12
 * <p>
 * 预约导航
 */
public class NavigationActivity extends BaseActivity implements View.OnClickListener, NavigationContract.View, TextWatcher, Inputtips.InputtipsListener, AdapterView.OnItemClickListener, AMap.InfoWindowAdapter, AMap.OnMarkerClickListener, AMap.OnMapClickListener, GeocodeSearch.OnGeocodeSearchListener {
    @Bind(R.id.listview)
    ListView listView;

    MapView mMapView = null;
    AMap aMap;

    //地理编码
    private GeocodeSearch geocoderSearch;

    private LatLonPoint latLonPoint;

    private Marker regeoMarker;

    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    private AMapLocationClientOption mLocationOption = null;

    AppCompatEditText searchEditText;

    NavigationContract.Presenter mPresenter;

    AutoCompleteTextAdapter adapter;

    View rootView;

    private String deviceId;

    private String deviceName;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_navigation;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((TextView) findViewById(R.id.title)).setText("预约导航");
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        searchEditText = (AppCompatEditText) findViewById(R.id.keyWord);
        searchEditText.addTextChangedListener(this);// 添加文本输入框监听事件
        rootView = getWindow().getDecorView();
        initMapView(savedInstanceState);
    }

    void initMapView(Bundle savedInstanceState) {
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        // 设置点击infoWindow事件监听器
//        aMap.setOnInfoWindowClickListener(this);
        // 设置自定义InfoWindow样式
        aMap.setInfoWindowAdapter(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setOnMapClickListener(this);

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);

        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);//设置放大缩小图标是否显示

        uiSettings.setCompassEnabled(false);//指南针显示
        uiSettings.setScaleControlsEnabled(true);//控制比例尺控件是否显示

//        aMap.setLocationSource(this);//通过aMap对象设置定位数据源的监听
//        uiSettings.setMyLocationButtonEnabled(true);
//        aMap.setMyLocationEnabled(true);// 可触发定位并显示当前位置
        initLocation();
        startLocation();
    }

    @Override
    protected void initData() {
        new NavigationPresenter(this);
        adapter = new AutoCompleteTextAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            deviceName = getIntent().getStringExtra("deviceName");
        }
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        //初始化client
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(getApplicationContext());
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
        myLocationStyle.interval(2000);//设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//定位一次，且将视角移动到地图中心点。
        myLocationStyle.showMyLocation(false);//是否显示定位蓝点
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                .decodeResource(getResources(), R.drawable.gps_point)));
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
    }

    private void startLocation(){
            mLocationClient.startLocation();
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
                StringBuilder sb = new StringBuilder();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    Logger.d(TAG, "onLocationChanged success，location ：" + new Gson().toJson(location));
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.gps_point));
                    aMap.addMarker(new MarkerOptions()
                            .title("location")
                            .position(latLng)
                            .icon(descriptor)
                            .draggable(true)
                            .setFlat(true));//设置marker平贴地图效果
                    CameraUpdate cameraupdate = CameraUpdateFactory.newLatLng(latLng);
                    aMap.moveCamera(cameraupdate);
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(aMap.getMaxZoomLevel() - 5));
                } else {
                    Logger.e(TAG, "onLocationChanged error，location ：" + new Gson().toJson(location));
                }
            }
        }
    };

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
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mPresenter.unsubscribe();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
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
    public void setPresenter(NavigationContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        if (!TextUtils.isEmpty(newText)) {
            InputtipsQuery inputquery = new InputtipsQuery(newText, "");
            Inputtips inputTips = new Inputtips(NavigationActivity.this, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onGetInputtips(List<Tip> tipList, int code) {
        if (code == AMapException.CODE_AMAP_SUCCESS && tipList.size() > 0) {// 正确返回
            adapter.setDataList(tipList);
            listView.setVisibility(View.VISIBLE);
            setListViewHeightBasedOnChildren(listView);
            Logger.i(TAG, "onGetInputtips：" + new Gson().toJson(tipList));
        } else {
            ToastUtils.showShort(this, code);
            listView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //隐藏软键盘
        KeyboardUtils.hideSoftInput(view);
        listView.setVisibility(View.GONE);
        LatLonPoint latLonPoint = adapter.getDataList().get(position).getPoint();
        if (latLonPoint == null || latLonPoint.getLatitude() == 0 || latLonPoint.getLongitude() == 0) {
            Logger.e(TAG, "onItemClick LatLonPoint is null !");
            ToastUtils.showShort(this,"未找到经纬度，请更换或输入更精确的地址");
            return;
        }
        LatLng latLng = new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
        CameraUpdate cameraupdate = CameraUpdateFactory.newLatLng(latLng);
        aMap.moveCamera(cameraupdate);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(aMap.getMaxZoomLevel() - 6));

        MarkerOptions markerOption = new MarkerOptions()
                .title(adapter.getDataList().get(position).getName())
                .snippet(adapter.getDataList().get(position).getAddress())
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.btn_maplocation_current)))
                .setFlat(true)//设置marker平贴地图效果
                .draggable(true);
        regeoMarker = aMap.addMarker(markerOption);
        regeoMarker.showInfoWindow();
    }

    @Override
    public void onBackPressed() {
        if (listView.getVisibility() == View.VISIBLE) {
            listView.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }

    /**
     * 设置listview高度
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        if (height >= 1000) {
            params.height = 1000;
        } else {
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        listView.setLayoutParams(params);
//        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10); // 可删除   listView.setLayoutParams(params); }
    }


    View infoWindow = null;

    @Override
    public View getInfoWindow(Marker marker) {
        if (infoWindow == null) {
            infoWindow = LayoutInflater.from(this).inflate(
                    R.layout.dialog_map_navigation_infowindow, null);
        }
        render(marker, infoWindow);
        return infoWindow;
    }

    /**
     * 自定义infowinfow窗口
     */
    public void render(final Marker marker, View view) {
        //如果想修改自定义Infow中内容，请通过view找到它并修改
        TextView content = view.findViewById(R.id.content);
        content.setText(TextUtils.isEmpty(marker.getSnippet())?marker.getTitle():marker.getSnippet());
        view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marker.hideInfoWindow();
                mPresenter.navigation(deviceId, marker.getTitle(), marker.getSnippet(), marker.getPosition().latitude, marker.getPosition().longitude);
            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marker.hideInfoWindow();
            }
        });
    }

    /**
     * 监听自定义infowindow窗口的infocontents事件回调
     *
     * @param marker
     * @return
     */
    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void navigationSuccess() {
        new CustomDialog(this)
                .setTitle("发送成功")
                .setMessage("目的地已发送至" + deviceName + "的后视镜。")
                .setCompleteButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).showDialog();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if("location".equals(marker.getTitle())){
            return true;
        }
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        latLonPoint = new LatLonPoint(latLng.latitude,latLng.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
    }

    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                String addressName = result.getRegeocodeAddress().getFormatAddress()
                        + "附近";
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        MapUtils.convertToLatLng(latLonPoint), 15));

                if(regeoMarker == null){
                    MarkerOptions markerOption = new MarkerOptions()
                            .title("")
                            .snippet(addressName)
                            .position(MapUtils.convertToLatLng(latLonPoint))
                            .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.btn_maplocation_current)))
                            .setFlat(true)//设置marker平贴地图效果
                            .draggable(true);
                    regeoMarker = aMap.addMarker(markerOption);
                    regeoMarker.showInfoWindow();
                }else {
                    regeoMarker.setTitle("");
                    regeoMarker.setSnippet(addressName);
                    regeoMarker.setPosition(MapUtils.convertToLatLng(latLonPoint));
                    regeoMarker.showInfoWindow();
                }

            } else {
                ToastUtils.showShort(NavigationActivity.this, R.string.no_result);
            }
        } else {
            ToastUtils.showShort(this, rCode);
        }
    }

    /**
     * 地理编码查询回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {

    }
}
