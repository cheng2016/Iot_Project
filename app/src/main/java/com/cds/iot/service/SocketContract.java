package com.cds.iot.service;

import android.app.NotificationManager;

import io.netty.bootstrap.Bootstrap;

/**
 * @author Chengzj
 * @date 2018/9/29 10:18
 */
public interface SocketContract {
    void startPushService();

    void checkNofificationPermission(NotificationManager manager);

    void showExitDialog();
}
