package com.cds.iot.module.fence.edit;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
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
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.TextOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.data.entity.FenceInfo;
import com.cds.iot.util.Logger;
import com.cds.iot.util.ToastUtils;
import com.cds.iot.view.MonitorTimeDialog;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * @author Chengzj
 * @date 2018/9/5 16:48
 * <p>
 * 电子围栏编辑页面
 */
public class FenceEditActivity extends BaseActivity implements FenceEditContract.View, View.OnClickListener, TextWatcher, Inputtips.InputtipsListener {
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


    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;

    //声明AMapLocationClientOption对象
    private AMapLocationClientOption mLocationOption = null;

    LatLng mLatLng;

    int mRadius = 10000;

    float mPosX, mPosY;
    float mCurPosX, mCurPosY;

    boolean isEditMark = false;

    FenceEditContract.Presenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_electric_fence_edit;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.right_button).setVisibility(View.VISIBLE);
        AppCompatTextView rightTv = (AppCompatTextView) findViewById(R.id.right_tv);
        rightTv.setVisibility(View.VISIBLE);
        rightTv.setTextColor(getResources().getColor(R.color.theme_color));
        rightTv.setText("保存");
        findViewById(R.id.search).setOnClickListener(this);
        timeButton.setOnClickListener(this);
        findViewById(R.id.localtionBtn).setOnClickListener(this);
        searchEditText = (AppCompatAutoCompleteTextView) findViewById(R.id.keyWord);
        searchEditText.addTextChangedListener(this);// 添加文本输入框监听事件

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        //参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
//        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(39.977290,116.337000),18,30,0));
//        aMap.moveCamera(mCameraUpdate);

        mLatLng = new LatLng(39.90845800214396, 116.4065925234351);

        drawNormalMark(mLatLng, mRadius);

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
                                drawEditMark(mLatLng, mRadius + scaleY * 4);
                            } else if (mCurPosY - mPosY < 0
                                    && (Math.abs(mCurPosY - mPosY) > 25)) {
                                //向上滑动
                                drawEditMark(mLatLng, mRadius - scaleY * 4);
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
        if(mLocationClient == null){
            mLocationClient = new AMapLocationClient(this.getApplicationContext());
        }
        if(mLocationOption == null){
            mLocationOption = getDefaultOption();
        }
        //设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        // 设置定位监听
        mLocationClient.setLocationListener(mLocationListener);
        //蓝点初始化
        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000);//设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;//定位一次，且将视角移动到地图中心点。
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
                    Logger.e(TAG, "onLocationChanged success，location ：" + new Gson().toJson(location));
                    CameraUpdate cameraupdate = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                    aMap.moveCamera(cameraupdate);

//                    MyLocationStyle myLocationStyle = new MyLocationStyle();
//                    myLocationStyle.getMyLocationIcon();
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
        Circle circle = aMap.addCircle(new CircleOptions().
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
    protected void initData() {
        new FenceEditPresenter(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            FenceInfo fenceInfo = (FenceInfo) getIntent().getExtras().getSerializable("fenceInfo");
            placeName.setText(fenceInfo.getName());
            placeDetail.setText(fenceInfo.getAddress());
            placeRange.setText("直径" + fenceInfo.getRadius() + "米");
        }

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
                ToastUtils.showShort(FenceEditActivity.this, "onclick");
                break;
            case R.id.search:

                break;
            case R.id.time_button:
                new MonitorTimeDialog(FenceEditActivity.this)
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        }).setCancelButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).showDialog();
                break;
            case R.id.localtionBtn:
                initLocation();
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
}
