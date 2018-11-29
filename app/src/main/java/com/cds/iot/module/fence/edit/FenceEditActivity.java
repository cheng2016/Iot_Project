package com.cds.iot.module.fence.edit;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.TextOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.data.Constant;
import com.cds.iot.data.entity.FenceInfo;
import com.cds.iot.util.Logger;
import com.cds.iot.util.ToastUtils;
import com.cds.iot.view.MonitorTimeDialog;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.Bind;

/**
 * @author Chengzj
 * @date 2018/9/5 16:48
 * <p>
 * 电子围栏编辑页面
 */
public class FenceEditActivity extends BaseActivity implements FenceEditContract.View, View.OnClickListener, TextWatcher, Inputtips.InputtipsListener, GeocodeSearch.OnGeocodeSearchListener {
    @Bind(R.id.place_name)
    AppCompatEditText placeName;
    @Bind(R.id.place_range)
    AppCompatTextView placeRange;
    @Bind(R.id.place_detail)
    AppCompatTextView placeDetail;
    @Bind(R.id.time_button)
    AppCompatTextView timeButton;

    AppCompatAutoCompleteTextView searchEditText;

    MapView mMapView = null;
    AMap aMap;

    LocationSource.OnLocationChangedListener mOnLocationChangedListener;
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    private AMapLocationClientOption mLocationOption = null;
    //第一次成功定位标识
    private boolean isFirstLoc = true;

    //地理编码
    private GeocodeSearch geocoderSearch;

    LatLng mLatLng;

    int mRadius = 1000;

    float mPosX, mPosY;
    float mCurPosX, mCurPosY;

    boolean isEditMark = false;

    FenceEditContract.Presenter mPresenter;

    String deviceId;

    FenceInfo mFenceInfo;

    String[] mVals = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_electric_fence_edit;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.right_button).setVisibility(View.VISIBLE);
        findViewById(R.id.right_button).setOnClickListener(this);
        AppCompatTextView rightTv = (AppCompatTextView) findViewById(R.id.right_tv);
        rightTv.setVisibility(View.VISIBLE);
        rightTv.setTextColor(getResources().getColor(R.color.theme_color));
        rightTv.setText("保存");
        findViewById(R.id.search).setOnClickListener(this);
        timeButton.setOnClickListener(this);
        findViewById(R.id.localtionBtn).setOnClickListener(this);
        searchEditText = (AppCompatAutoCompleteTextView) findViewById(R.id.keyWord);
        searchEditText.addTextChangedListener(this);// 添加文本输入框监听事件
        initMapView(savedInstanceState);
    }

    @Override
    protected void initData() {
        new FenceEditPresenter(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
            if (!TextUtils.isEmpty(deviceId)) {
                mFenceInfo = new FenceInfo();
                mFenceInfo.setDevice_id(deviceId);
                mFenceInfo.setType(Constant.FENCE_TYPE_OTHER);
                mFenceInfo.setRepeat_date("0000000");
                mLatLng = new LatLng(39.90845800214396, 116.4065925234351);
                initDrawMark(mLatLng);
                placeName.setText("");
                placeDetail.setText("");
                placeRange.setText("");
                placeName.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.icn_temporary_circular), null, null, null);
            } else {
                mFenceInfo = (FenceInfo) getIntent().getExtras().getSerializable("fenceInfo");
                placeName.setText(mFenceInfo.getName());
                placeDetail.setText(mFenceInfo.getAddress());
                placeRange.setText("直径" + mFenceInfo.getRadius() + "米");
                if (mFenceInfo.getType() == Constant.FENCE_TYPE_HOME) {
                    placeName.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.icn_home_circular), null, null, null);
                }else if (mFenceInfo.getType() == Constant.FENCE_TYPE_COMPANY){
                    placeName.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.icn_company_circular), null, null, null);
                }
                StringBuilder days = new StringBuilder();
                for (int i = 0; i < 7; i++) {
                    if ("1".equals(mFenceInfo.getRepeat_date().substring(i, i + 1))) {
                        days.append(mVals[i]).append("\r");
                    }
                }
                days.append(mFenceInfo.getBegin_time()).append("-").append(mFenceInfo.getEnd_time());
                String time = "<font color='#999999'>监控时间 :</font>" + days.toString();
                timeButton.setText(Html.fromHtml(time));
                if (!TextUtils.isEmpty(mFenceInfo.getCenter_location())) {
                    String[] strings = mFenceInfo.getCenter_location().split(",");
                    mLatLng = new LatLng(Double.valueOf(strings[1]), Double.valueOf(strings[0]));
                    initDrawMark(mLatLng);
                }
            }
        }
        //初始化定位
        initLocation();
    }

    void initDrawMark(LatLng lalng) {
        drawNormalMark(lalng, mRadius);
        CameraUpdate cameraupdate = CameraUpdateFactory.newLatLng(mLatLng);
        aMap.moveCamera(cameraupdate);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(aMap.getMaxZoomLevel() - 6));
    }

    protected void initMapView(Bundle savedInstanceState) {
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);//设置放大缩小图标是否显示
        uiSettings.setMyLocationButtonEnabled(false);// 定位按钮是否显示
        uiSettings.setCompassEnabled(false);//指南针是否显示
        uiSettings.setScaleControlsEnabled(true);//控制比例尺控件是否显示

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);

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

        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Logger.i(TAG, "onMapClick：{ latitude：" + latLng.latitude + "，longitude：" + latLng.longitude + " }");
            }
        });
        aMap.setOnMapLongClickListener(new AMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Logger.i(TAG, "onMapLongClick：{ latitude：" + latLng.latitude + "，longitude：" + latLng.longitude + " }");

                float distance = AMapUtils.calculateLineDistance(mLatLng, latLng);

                if (distance < mRadius) {
                    aMap.getUiSettings().setScrollGesturesEnabled(false);
                    isEditMark = true;
                    drawEditMark(mLatLng, mRadius);
                }
            }
        });
        aMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        mPosX = event.getX();
                        mPosY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurPosX = event.getX();
                        mCurPosY = event.getY();

                        int scaleY = (int) Math.abs(mCurPosY - mPosY);

                        if (isEditMark) {
                            if (mCurPosY - mPosY > 0
                                    && (Math.abs(mCurPosY - mPosY) > 25)) {
                                //向下滑動
                                drawEditMark(mLatLng, mRadius + scaleY * 3);
                            } else if (mCurPosY - mPosY < 0
                                    && (Math.abs(mCurPosY - mPosY) > 25)) {
                                //向上滑动
                                drawEditMark(mLatLng, mRadius - scaleY * 3);
                            }
                        } else {
                            mLatLng = getMapCenterPoint();
                            drawNormalMark(mLatLng, mRadius);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        isEditMark = false;
                        aMap.getUiSettings().setScrollGesturesEnabled(true);
                        drawNormalMark(mLatLng, mRadius);
                        placeRange.setText("直径" + mRadius + "米");
                        mFenceInfo.setRadius(String.valueOf(mRadius));
                        LatLonPoint latLonPoint = new LatLonPoint(mLatLng.latitude, mLatLng.longitude);
                        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
                        break;
                }
            }
        });
    }

    /**
     * 初始化定位
     */
    public void initLocation() {
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

    //声明定位回调监听器
    private final AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                StringBuilder sb = new StringBuilder();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    mOnLocationChangedListener.onLocationChanged(location);

                    Logger.d(TAG, "onLocationChanged success，location ：" + new Gson().toJson(location));
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

    void drawEditMark(LatLng latLng, int radius) {
        if (radius > 1000) {
            radius = 1000;
            ToastUtils.showShort(FenceEditActivity.this, "电子围栏最大范围为1000m！");
        } else if (radius < 100) {
            radius = 100;
            ToastUtils.showShort(FenceEditActivity.this, "电子围栏最小范围为200m！");
        }
        this.mRadius = radius;
        aMap.clear();
        mMapView.invalidate();
        aMap.addCircle(new CircleOptions().
                center(latLng).
                radius(radius).
                fillColor(Color.argb(100, 34, 230, 169)).
                strokeColor(Color.argb(100, 34, 230, 169)).
                strokeWidth(15));

        aMap.addText(new TextOptions().
                position(latLng).
                backgroundColor(getResources().getColor(R.color.transparent)).
                fontColor(getResources().getColor(R.color.white)).
                fontSize(getResources().getDimensionPixelSize(R.dimen.font_size_18)).
                text(mRadius * 2 + "m"));
    }

    void drawNormalMark(LatLng latLng, int radius) {
        aMap.clear();
        mMapView.invalidate();
        aMap.addCircle(new CircleOptions().
                center(mLatLng).
                radius(mRadius).
                fillColor(Color.argb(100, 48, 214, 220)).
                strokeColor(Color.argb(100, 48, 214, 220)).
                strokeWidth(15));

/*        Marker marker = aMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                .position(mLatLng)
                .snippet("详细信息")
                .draggable(true));
        marker.showInfoWindow();*/
        aMap.addText(new TextOptions().
                position(mLatLng).
                backgroundColor(getResources().getColor(R.color.transparent)).
                fontColor(getResources().getColor(R.color.white)).
                fontSize(getResources().getDimensionPixelSize(R.dimen.font_size_18)).
                text(mRadius * 2 + "m"));
    }


    /**
     * by moos on 2017/09/05
     * func:获取屏幕中心的经纬度坐标
     *
     * @return
     */
    public LatLng getMapCenterPoint() {
        int left = mMapView.getLeft();
        int top = mMapView.getTop();
        int right = mMapView.getRight();
        int bottom = mMapView.getBottom();
        // 获得屏幕点击的位置
        int x = (int) (mMapView.getX() + (right - left) / 2);
        int y = (int) (mMapView.getY() + (bottom - top) / 2);
        Projection projection = aMap.getProjection();
        LatLng pt = projection.fromScreenLocation(new Point(x, y));
        return pt;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
        mMapView.onDestroy();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.right_button:
                if (mFenceInfo != null) {
                    mFenceInfo.setName(placeName.getText().toString());
                    mFenceInfo.setCenter_location(mLatLng.longitude + "," + mLatLng.latitude);
                    mPresenter.saveFenceInfo(mFenceInfo);
                }
                break;
            case R.id.search:

                break;
            case R.id.time_button:
                new MonitorTimeDialog(FenceEditActivity.this)
                        .setTime(mFenceInfo.getBegin_time(), mFenceInfo.getEnd_time())
                        .setSelectWeek(mFenceInfo.getRepeat_date())
                        .setPositiveButton("确定", new MonitorTimeDialog.OnMonitorClickListener() {
                            @Override
                            public void onOnMonitorClick(Set<Integer> selectPosSet, String start, String end) {
                                String week = mFenceInfo.getRepeat_date();
                                for (int i = 0; i < 7; i++) {
                                    StringBuilder sb = new StringBuilder(week);
                                    sb.replace(i, i + 1, "0");
                                    for (Integer j : selectPosSet) {
                                        sb.replace(j, j + 1, "1");
                                    }
                                    week = sb.toString();
                                }
                                mFenceInfo.setRepeat_date(week);
                                mFenceInfo.setBegin_time(start);
                                mFenceInfo.setEnd_time(end);

                                StringBuilder days = new StringBuilder();
                                for (int i = 0; i < 7; i++) {
                                    if ("1".equals(mFenceInfo.getRepeat_date().substring(i, i + 1))) {
                                        days.append(mVals[i]).append("\r");
                                    }
                                }
                                days.append(start).append("-").append(end);
                                String time = "<font color='#999999'>监控时间 :</font>" + days.toString();
                                timeButton.setText(Html.fromHtml(time));
                            }
                        }).showDialog();
                break;
            case R.id.localtionBtn:
                mLocationClient.startLocation();
                break;
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        if (!TextUtils.isEmpty(newText)) {
            InputtipsQuery inputquery = new InputtipsQuery(newText, "");
            Inputtips inputTips = new Inputtips(FenceEditActivity.this, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        Logger.i(TAG, "afterTextChanged  editable：" + editable.toString());
    }

    @Override
    public void onGetInputtips(List<Tip> tipList, int code) {
        if (code == AMapException.CODE_AMAP_SUCCESS) {// 正确返回
            List<String> listString = new ArrayList<>();
            for (int i = 0; i < tipList.size(); i++) {
                listString.add(tipList.get(i).getName());
            }
            ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
                    getApplicationContext(),
                    R.layout.route_inputs, listString);
            searchEditText.setAdapter(aAdapter);
            aAdapter.notifyDataSetChanged();
        } else {
            ToastUtils.showShort(this, code);
        }
    }

    @Override
    public void setPresenter(FenceEditContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                String addressName = result.getRegeocodeAddress().getFormatAddress();
                mFenceInfo.setAddress(addressName);
                placeDetail.setText(addressName);
            } else {
                ToastUtils.showShort(FenceEditActivity.this, R.string.no_result);
            }
        } else {
            ToastUtils.showShort(this, rCode);
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
    }

    @Override
    public void saveFenceInfoSuccess() {
        if (TextUtils.isEmpty(mFenceInfo.getId())) {
            ToastUtils.showShort(this, "添加电子围栏成功");
        } else {
            ToastUtils.showShort(this, "修改电子围栏成功");

        }
        setResult(RESULT_OK);
        finish();
    }
}
