package com.cds.iot.module.location;

public interface LocationStrategy {

    void requestLocation();

    void stopLocation();

    void destoryLocation();

    void setListener(UpdateLocationListener listener);
}
