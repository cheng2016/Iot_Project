package com.cds.iot.module.device.mirror.track;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
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
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.data.entity.TrackPath;
import com.cds.iot.data.entity.TrackResp;
import com.cds.iot.util.Logger;
import com.cds.iot.view.CustomDialog;
import com.cds.iot.view.MyDatePickerDialog;
import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author Chengzj
 * @date 2018/10/10 15:10
 */
public class TrackActivity extends BaseActivity implements View.OnClickListener, TrackContract.View, SeekBar.OnSeekBarChangeListener, SmoothMoveMarker.MoveListener, AMap.OnMarkerClickListener, AMap.InfoWindowAdapter {
    @Bind(R.id.before_day)
    AppCompatTextView beforeDay;
    @Bind(R.id.select_day)
    AppCompatTextView selectDay;
    @Bind(R.id.after_day)
    AppCompatTextView afterDay;
    @Bind(R.id.play_btn)
    AppCompatImageView playBtn;
    @Bind(R.id.seek_bar)
    SeekBar seekBar;

    boolean isPlayer = false;

    MapView mMapView = null;
    AMap aMap;

    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    private AMapLocationClientOption mLocationOption = null;

    TrackContract.Presenter mPresenter;

    DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd");

    DateFormat DATE_FORMAT_STR = new SimpleDateFormat("yyyy-MM-dd");

    DateFormat DATE_FORMAT_MARK = new SimpleDateFormat("yyyy.MM.dd  HH:mm");

    int year = 2018;
    int month = 8;
    int dayOfMonth = 19;

    String deviceId = "";

    SmoothMoveMarker smoothMarker;

    List<TrackPath> trackPaths;

    List<LatLng> points;

    List<Marker> markers;

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
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
            mLocationClient = null;
            mLocationOption = null;
        }
        if (null != smoothMarker) {
            smoothMarker.stopMove();
            smoothMarker.destroy();
            smoothMarker = null;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_track;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((TextView) findViewById(R.id.title)).setText("行车轨迹");
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        playBtn.setOnClickListener(this);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);//设置放大缩小图标是否显示
        uiSettings.setCompassEnabled(false);//指南针显示
        uiSettings.setScaleControlsEnabled(true);//控制比例尺控件是否显示
        // 设置自定义InfoWindow样式
        aMap.setInfoWindowAdapter(this);
        aMap.setOnMarkerClickListener(this);
    }

    @Override
    protected void initData() {
        new TrackPresenter(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            deviceId = getIntent().getStringExtra("deviceId");
        }
        initLocation();
        startLocation();
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

    private void startLocation() {
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
            case R.id.play_btn:
                if (!isPlayer) {
                    if (smoothMarker == null) {
                        return;
                    }
                    // 开始滑动
                    smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.mipmap.icn_mycar));
//                    smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.drawable.icon_car));
                    smoothMarker.startSmoothMove();
                    isPlayer = true;
                    playBtn.setImageResource(R.mipmap.btn_stopplay);
                } else {
                    if (smoothMarker == null) {
                        return;
                    }
                    // 停止滑动
                    smoothMarker.stopMove();
                    isPlayer = false;
                    playBtn.setImageResource(R.mipmap.btn_playtrace);
                }
                break;
            default:
                break;
        }
    }

    MyDatePickerDialog datePickerDialog;

    @OnClick({R.id.before_day, R.id.date_layout, R.id.after_day})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.before_day:
                Logger.i(TAG, "year：" + year + " month：" + month + " dayOfMonth：" + dayOfMonth);
                LocalDate yesterdayDate = new LocalDate(year, month, dayOfMonth);
                DateTime yesterday = new DateTime(yesterdayDate.toDate()).minusDays(1);
                year = yesterday.getYear();
                month = yesterday.getMonthOfYear();
                dayOfMonth = yesterday.getDayOfMonth();
                selectDay.setText(DATE_FORMAT.format(yesterday.toDate()));
                mPresenter.getTrack(deviceId, DATE_FORMAT_STR.format(yesterday.toDate()));
                break;
            case R.id.date_layout:
                Logger.i(TAG, "date_layout year：" + year + " month：" + month + " dayOfMonth：" + dayOfMonth);
/*                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int y, int m, int d) {
                        Logger.i(TAG, "onDateSet year：" + y + " month：" + m + " dayOfMonth：" + d);
                        year = y;
                        month = ++m;
                        dayOfMonth = d;
                        LocalDate localDate = new LocalDate(year, month, dayOfMonth);
                        selectDay.setText(DATE_FORMAT.format(localDate.toDate()));
                        mPresenter.getTrack(deviceId, DATE_FORMAT_STR.format(localDate.toDate()));
                    }
                }, year, month - 1, dayOfMonth);
                datePickerDialog.show();*/

                datePickerDialog = new MyDatePickerDialog(this).setDatePickerListener(new MyDatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(cn.aigestudio.datepicker.views.DatePicker view, String dateStr) {
//                        ToastUtils.showShort(TrackActivity.this,dateStr);
                        String[] strings = dateStr.split("-");
                        year = Integer.parseInt(strings[0]);
                        month = Integer.parseInt(strings[1]);
                        dayOfMonth = Integer.parseInt(strings[2]);
                        LocalDate localDate = new LocalDate(year, month, dayOfMonth);
                        selectDay.setText(DATE_FORMAT.format(localDate.toDate()));
                        mPresenter.getTrack(deviceId, DATE_FORMAT_STR.format(localDate.toDate()));
                    }
                }).setMothOfYear(year, month);
                datePickerDialog.show();
                break;
            case R.id.after_day:
                Logger.i(TAG, "year：" + year + " month：" + month + " dayOfMonth：" + dayOfMonth);
                LocalDate afterlDate = new LocalDate(year, month, dayOfMonth);
                DateTime afterDay = new DateTime(afterlDate.toDate()).plusDays(1);
                year = afterDay.getYear();
                month = afterDay.getMonthOfYear();
                dayOfMonth = afterDay.getDayOfMonth();
                selectDay.setText(DATE_FORMAT.format(afterDay.toDate()));
                mPresenter.getTrack(deviceId, DATE_FORMAT_STR.format(afterDay.toDate()));
                break;
        }
    }

    @Override
    public void setPresenter(TrackContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private void resetView() {
        currentMark = null;
        points = null;
        markers = null;
        trackPaths = null;
        smoothMarker = null;
        isPlayer = false;
        playBtn.setImageResource(R.mipmap.btn_playtrace);
        seekBar.setProgress(0);
        aMap.clear();
        mMapView.invalidate();
    }


    @Override
    public void getTrackSuccess(TrackResp resp) {
        resetView();
        if (resp != null && resp.getTrack_path() != null && resp.getTrack_path().size() > 0) {
            datePickerDialog.dismiss();
            points = new ArrayList<>();
            markers = new ArrayList<>();
            trackPaths = resp.getTrack_path();
            MarkerOptions markerOption = new MarkerOptions();
            for (int i = 0; i < resp.getTrack_path().size(); i++) {
                TrackPath path = resp.getTrack_path().get(i);
                String[] split = path.getLocation().split(",");
                double longtitude = Double.valueOf(split[0]);
                double latitude = Double.valueOf(split[1]);
                LatLng latLng = new LatLng(latitude, longtitude);
                points.add(latLng);
                markerOption
                        .position(latLng)
                        .title(path.getAddress())
                        .snippet(DATE_FORMAT_MARK.format(new Date(Long.valueOf(path.getTime()))));
                if (i == 0) {
                    markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(), R.mipmap.icn_tracingpoint_current)));
                    currentMark = aMap.addMarker(markerOption);
                    markers.add(currentMark);
                } else {
                    markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(), R.mipmap.icn_tracingpoint)));
                    markers.add(aMap.addMarker(markerOption));
                }
            }

            for (TrackPath path : resp.getTrack_path()) {
                String[] split = path.getLocation().split(",");
                double longtitude = Double.valueOf(split[0]);
                double latitude = Double.valueOf(split[1]);
                LatLng latLng = new LatLng(latitude, longtitude);
                points.add(latLng);
                markerOption
                        .position(latLng)
                        .title(path.getAddress())
                        .snippet(DATE_FORMAT_MARK.format(new Date(Long.valueOf(path.getTime()))));
                markers.add(aMap.addMarker(markerOption));
            }

            Polyline polyline = aMap
                    .addPolyline(new PolylineOptions()
                            .addAll(points)
                            .width(10)
                            .color(Color.argb(255, 232, 89, 89)));

            //多个Marker标记自动缩放全部显示在屏幕中
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();//存放所有点的经纬度
            for (int i = 0; i < markers.size(); i++) {
                boundsBuilder.include(markers.get(i).getPosition());//把所有点都include进去（LatLng类型）
            }
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 10));

            smoothMarker = new SmoothMoveMarker(aMap);
            // 设置滑动的图标
            smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.mipmap.icn_mycar));
//            smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.drawable.icon_car));

            LatLng drivePoint = points.get(0);
            Pair<Integer, LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(points, drivePoint);
            points.set(pair.first, drivePoint);
            List<LatLng> subList = points.subList(pair.first, points.size());
            // 设置滑动的轨迹左边点
            smoothMarker.setPoints(subList);
            // 设置滑动的总时间
            smoothMarker.setTotalDuration(points.size());
            smoothMarker.setMoveListener(this);

            Logger.i(TAG, "TotalDuration：" + points.size());
            seekBar.setMax(points.size() - 1);
            //seekBar设置滑动事件
            seekBar.setOnSeekBarChangeListener(this);
        } else {
            new CustomDialog(this)
                    .setTitle("暂无轨迹信息")
                    .setMessage("该段时间内无任何轨迹信息")
                    .setCompleteButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).showDialog();
        }
    }

    @Override
    public void move(double v) {
        Logger.i(TAG, "move：" + v);
        Logger.i(TAG, "index：" + smoothMarker.getIndex());
        seekBar.setProgress(smoothMarker.getIndex() + 1);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Logger.i(TAG, "onProgressChanged：progress：" + progress);
        if (points != null && points.size() > 0) {
//            CameraUpdate cameraupdate = CameraUpdateFactory.newLatLng(points.get(progress));
//            aMap.moveCamera(cameraupdate);

            if (currentMark != null) {
                currentMark.hideInfoWindow();
                currentMark.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.mipmap.icn_tracingpoint)));
            }
            Marker marker = markers.get(progress);
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.mipmap.icn_tracingpoint_current)));
            currentMark = marker;
//            marker.showInfoWindow();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Logger.i(TAG, "onStartTrackingTouch 开始拖动");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Logger.i(TAG, "onStopTrackingTouch 停止拖动");
    }

    Marker currentMark;

    @Override
    public boolean onMarkerClick(Marker marker) {
        if ("location".equals(marker.getTitle())) {
            return true;
        }
        if (currentMark != null) {
            currentMark.hideInfoWindow();
            currentMark.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.mipmap.icn_tracingpoint)));
        }
        currentMark = marker;
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.mipmap.icn_tracingpoint_current)));
        marker.showInfoWindow();
        return false;
    }

    View infoWindow = null;

    @Override
    public View getInfoWindow(Marker marker) {
        if (infoWindow == null) {
            infoWindow = LayoutInflater.from(this).inflate(
                    R.layout.dialog_map_track_infowindow, null);
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
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marker.hideInfoWindow();
            }
        });
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
