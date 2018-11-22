package com.cds.iot.module.location;

import com.amap.api.location.AMapLocation;

public interface UpdateLocationListener {
    void updateLocationChanged(AMapLocation location, int gpsCount);
}
