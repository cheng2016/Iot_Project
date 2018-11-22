package com.cds.iot.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import java.io.File;

/**
 * Created by chengzj on 2018/6/21.
 */
public class DeviceUtils {

  /**
   * 获取设备MAC地址
   * 需添加权限<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
   */
  public static String getMacAddress(Context context) {
    String macAddress = null;
    WifiManager wifi = (WifiManager) context
            .getSystemService(Context.WIFI_SERVICE);
    WifiInfo info = wifi.getConnectionInfo();
    if (null != info) {
      macAddress = info.getMacAddress();
      if (null != macAddress) {
        macAddress = macAddress.replace(":", "");
      }
    }
    return macAddress;
  }

  /**
   * 获取设备厂商，如Xiaomi
   */
  public static String getManufacturer() {
    String MANUFACTURER = Build.MANUFACTURER;
    return MANUFACTURER;
  }

  /**
   * 获取设备型号，如MI2SC
   */
  public static String getModel() {
    String model = Build.MODEL;
    if (model != null) {
      model = model.trim().replaceAll("\\s*", "");
    } else {
      model = "";
    }
    return model;
  }

  /**
   * 判断sdcard是否被挂载
   */
  public static boolean isSDCardEnable() {
    return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
  }

  /**
   * 检测Sdcard是否存在
   *
   * @return
   */
  public static boolean isExitsSdcard() {
    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
      return true;
    else
      return false;
  }

  /**
   * 获取设备SD卡路径
   */
  public static String getSDCardPath() {
    return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
  }

  /**
   * 判断设备是否是手机
   */
  public static boolean isPhone(Context context) {
    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    return tm.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
  }

  /**
   * 获取当前设备的IMIE，需与上面的isPhone一起使用
   * 需添加权限<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
   */
  public static String getDeviceIMEI(Context context) {
    String deviceId;
    if (isPhone(context)) {
      TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
        ToastUtils.showShort(context, "没有相应权限！");
        return "";
      }
      deviceId = tm.getDeviceId();
    } else {
      deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
    return deviceId;
  }
}