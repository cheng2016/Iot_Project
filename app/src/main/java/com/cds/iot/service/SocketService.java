package com.cds.iot.service;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;

import com.cds.iot.App;
import com.cds.iot.R;
import com.cds.iot.data.Constant;
import com.cds.iot.data.entity.SMessage;
import com.cds.iot.data.source.local.SMessageDaoUtils;
import com.cds.iot.module.login.LoginActivity;
import com.cds.iot.module.main.MainActivity;
import com.cds.iot.service.message.ContentMsg;
import com.cds.iot.service.message.HeadMsg;
import com.cds.iot.service.message.TailMsg;
import com.cds.iot.util.AppManager;
import com.cds.iot.util.Logger;
import com.cds.iot.util.NetUtils;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.RomUtils;
import com.cds.iot.view.MyAlertDialog;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 绑定服务
 */
public class SocketService extends Service implements SocketContract {
    public final static String TAG = "SocketService";
    public static final String UP_MSG_END_FLAG = new String(new byte[]{0x01, 0x01, 0x01});

    public static final String END_FLAG = new String(new byte[]{0x01});

    public static final String TYPE_REPLY = "1";

    public static final String TYPE_CONNECT = "2";

    public static final String TYPE_HEART = "3";

    public static final String TYPE_UPDATE = "4";

    public static final String TYPE_LOGIN_OUT = "5";

    public String HEART_BEAT_STRING;

    private String userId;

    private String token;

    private SMessageDaoUtils daoUtils;

    public class MyBinder extends Binder {
        public SocketService getService() {
            return SocketService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i(TAG, "onCreate, Thread: " + Thread.currentThread().getName());
        registerActionReceiver();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Logger.i(TAG, "onBind, Thread: " + Thread.currentThread().getName());
        return new MyBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logger.i(TAG, "onUnbind, from:" + intent.getStringExtra("from"));
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.i(TAG, "onDestroy, Thread: " + Thread.currentThread().getName());
        unregisterActionReceiver();
    }

    //是否关闭
    private boolean isClose = false;

    @Override
    public void startPushService() {
        isClose = false;
        initPushService();
        Logger.w(TAG, "startPushService");
    }

    private EventLoopGroup eventLoopGroup;

    private ChannelFuture mChannelFuture;
    //是否连接
    private boolean isConnect = false;

    private synchronized void initPushService() {
        userId = PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID, "");
        token = PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.ACCESS_TOKEN, "");
        Logger.w(TAG, "initPushService, Thread: " + Thread.currentThread().getName() + " userId：" + userId);
        if (NetUtils.isConnected(this) && !TextUtils.isEmpty(userId)) {
            if (mChannelFuture == null || !isConnect) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bootstrap bootstrap = new Bootstrap();
                            eventLoopGroup = new NioEventLoopGroup();
                            bootstrap.group(eventLoopGroup);
                            bootstrap.channel(NioSocketChannel.class);
                            bootstrap.option(ChannelOption.TCP_NODELAY, true);
//                                    mBootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
                            bootstrap.option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 64 * 1024);
                            bootstrap.option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 32 * 1024);
                            bootstrap.handler(new ChannelInitializer() {
                                @Override
                                protected void initChannel(Channel ch) {
                                    ChannelPipeline pipeline = ch.pipeline();
                                    ByteBuf delimiter = Unpooled.copiedBuffer(UP_MSG_END_FLAG.getBytes());
                                    pipeline.addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, delimiter));
                                    pipeline.addLast(new IdleStateHandler(0,
                                            0, 2, TimeUnit.MINUTES));
                                    pipeline.addLast("decoder", new StringDecoder());
                                    pipeline.addLast("encoder", new StringEncoder());
                                    pipeline.addLast(new NettyClientHandler());
                                }
                            });
                            connect(bootstrap);
                        } catch (Exception e) {
                            isConnect = false;
                            Logger.e(TAG, "initNettySocket, Exception: ", e);
                        }
                    }
                }).start();
            }
        }
    }

    public synchronized void connect(final Bootstrap bootstrap) {
        Logger.w(TAG, "connect is excute!");
        userId = PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID, "");
        if (NetUtils.isConnected(this) && !TextUtils.isEmpty(userId)) {
            try {
                // 发起异步连接操作
                mChannelFuture = bootstrap.connect(App.getInstance().HOST, App.getInstance().PORT).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            isConnect = true;
                            Logger.i(TAG, "Connect to server successfully!");
                        } else {
                            isConnect = false;
                            Logger.i(TAG, "Failed to connect to server，After 10s, the connection will be reconnected");
                            future.channel().eventLoop().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    connect(bootstrap);
                                }
                            }, 10, TimeUnit.SECONDS);
                        }
                    }
                }).sync();
                // 当代客户端链路关闭
                mChannelFuture.channel().closeFuture().sync();
            } catch (Exception e) {
                Logger.e(TAG, "initNettySocket connect Exception: ", e);
                isConnect = false;
            }
        }
    }

    public void shutdownNetty() {
        isClose = true;
        Logger.e(TAG, "shutdownNetty!");
        isConnect = false;
        if (null != mChannelFuture && null != mChannelFuture.channel()) {
            mChannelFuture.channel().close();
            mChannelFuture = null;
        }
        if (null != eventLoopGroup) {
            eventLoopGroup.shutdownGracefully();
            eventLoopGroup = null;
        }
    }

    class NettyClientHandler extends ChannelInboundHandlerAdapter {
        SocketMsg socketMsg;

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
            Logger.i(TAG, "exceptionCaught：" + cause);
            ctx.close();
        }


        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            Logger.i(TAG, "channelActive 上线了");
            socketMsg = new SocketMsg();
            HeadMsg headMsg = new HeadMsg("", TYPE_CONNECT, userId, token);
            socketMsg.setHead(headMsg);
            socketMsg.setContent(new ContentMsg());
            socketMsg.setTail(new TailMsg(System.currentTimeMillis() + ""));
            Logger.i(TAG, "write connect：" + new Gson().toJson(socketMsg) + END_FLAG);
            ctx.writeAndFlush(new Gson().toJson(socketMsg) + END_FLAG);
            socketMsg.getHead().setMsg_type(TYPE_HEART);
            socketMsg.setTail(new TailMsg(System.currentTimeMillis() + ""));
            HEART_BEAT_STRING = new Gson().toJson(socketMsg) + END_FLAG;
            Logger.i(TAG, "write heart：" + HEART_BEAT_STRING);
            ctx.writeAndFlush(new Gson().toJson(socketMsg) + END_FLAG);
        }

        @Override
        public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
            super.channelRead(ctx, msg);
            String data = msg.toString();
            Logger.i(TAG, "client channelRead 数据内容：data：" + "\r\n" + data);

            SocketMsg response = new Gson().fromJson(data, SocketMsg.class);
            if (!("1".equals(response.getHead().getMsg_type()))) {
                socketMsg.getHead().setMsg_id(response.getHead().getMsg_id());
                socketMsg.getHead().setMsg_type(TYPE_REPLY);
                socketMsg.setContent(new ContentMsg());
                socketMsg.setTail(new TailMsg(System.currentTimeMillis() + ""));
                ctx.writeAndFlush(new Gson().toJson(socketMsg) + END_FLAG);
                Logger.i(TAG, "write response data：" + new Gson().toJson(socketMsg) + END_FLAG);

                if (TYPE_LOGIN_OUT.equals(response.getHead().getMsg_type())) {
                    isConnect = false;
                    showExitDialog();
                } else {
                    final SMessage sMessage = new SMessage();
                    sMessage.setUserId(response.getHead().getUser_id());
                    sMessage.setUid(response.getContent().getDetail().getUid());
                    sMessage.setMsgId(response.getHead().getMsg_id());
                    sMessage.setMsgType(response.getHead().getMsg_type());
                    sMessage.setTitle(response.getContent().getTitle());
                    sMessage.setContent(response.getContent().getDetail().getDetails());
                    sMessage.setTailtime(response.getTail().getTime());
                    sMessage.setDeviceId(response.getContent().getDetail().getDevice_id());
                    sMessage.setDeviceName(response.getContent().getDetail().getDevice_name());
                    sMessage.setDeviceImg(response.getContent().getDetail().getDevice_type_img());
                    sMessage.setDeviceType(response.getContent().getDetail().getDevice_type_id());
                    sMessage.setDeviceTypeName(response.getContent().getDetail().getDevice_type_name());
                    if (Constant.MESSAGE_TYPE_DEVICE_VIDEO == Integer.parseInt(response.getHead().getMsg_type())) {//视频的情况
                        sMessage.setPhotoUrl(response.getContent().getDetail().getFirst_img_url());
                        sMessage.setVideoUrl(response.getContent().getDetail().getUrl());
                    } else {
                        sMessage.setPhotoUrl(response.getContent().getDetail().getUrl());
                    }
                    if (daoUtils == null) {
                        daoUtils = new SMessageDaoUtils(App.getInstance());
                    }
                    daoUtils.insert(sMessage);

                    //发送广播更新界面
                    Intent intent = new Intent();
                    intent.setAction(MainActivity.MESSAGE_BROADCAST_ACTION);
                    intent.putExtra("messageType", sMessage.getMsgType());
                    intent.putExtra("title", sMessage.getTitle());
                    intent.putExtra("content", sMessage.getContent());
                    sendBroadcast(intent);
                    if (PreferenceUtils.getPrefBoolean(App.getInstance(), PreferenceConstants.MESSAGE_NOTIFY, true) && !TextUtils.isEmpty(PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID, ""))) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            createNotificationChannel(response.getContent().getTitle(), response.getContent().getDetail().getDetails());
                        } else {
                            createNotification(response.getContent().getTitle(), response.getContent().getDetail().getDetails());
                        }
                    }
                }
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            Logger.e(TAG, "channelInactive 掉线了");
            isConnect = false;
            sendMessage(HEART_BEAT_STRING);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            super.userEventTriggered(ctx, evt);
            socketMsg.getHead().setMsg_type(TYPE_HEART);
            socketMsg.setContent(new ContentMsg());
            socketMsg.setTail(new TailMsg(System.currentTimeMillis() + ""));
            HEART_BEAT_STRING = new Gson().toJson(socketMsg) + END_FLAG;

            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state().equals(IdleState.READER_IDLE)) {
                    Logger.i(TAG, "userEventTriggered READER_IDLE 读超时，长期没收到服务器推送数据！Channel is active：" + mChannelFuture.channel().isActive());
                    //可以选择重新连接
                    isConnect = false;
                    ctx.close();
                } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                    Logger.i(TAG, "userEventTriggered WRITER_IDLE 写超时，长期未向服务器发送数据！Channel is active：" + mChannelFuture.channel().isActive());
                    //发送心跳包
                    Logger.w(TAG, "sendMessage：" + HEART_BEAT_STRING);
                    ctx.writeAndFlush(HEART_BEAT_STRING);
                } else if (event.state().equals(IdleState.ALL_IDLE)) {
                    Logger.i(TAG, "userEventTriggered ALL 没有接收或发送数据一段时间");
                    //发送心跳包
                    Logger.w(TAG, "sendMessage：" + HEART_BEAT_STRING);
                    ctx.writeAndFlush(HEART_BEAT_STRING);
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String title, String message) {
        Logger.i(TAG, "createNotificationChannel title：" + title + " message：" + message);
        //创建通知管理类
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        checkNofificationPermission(manager);
        String CHANNEL_ID = PreferenceUtils.getPrefString(this, PreferenceConstants.CHANNEL_ID, "");
        String CHANNEL_NAME = PreferenceUtils.getPrefString(this, PreferenceConstants.CHANNEL_NAME, "");
        boolean isVibrate = PreferenceUtils.getPrefBoolean(App.getInstance(), PreferenceConstants.VIBRATE_NOTIFY, true);
        boolean isVoice = PreferenceUtils.getPrefBoolean(App.getInstance(), PreferenceConstants.VOICE_NOTIFY, true);
        int importance;
        if (RomUtils.isMiui()){
            if(isVoice && isVibrate){
                importance = NotificationManager.IMPORTANCE_HIGH;
            }else if(isVoice && !isVibrate){
                importance = NotificationManager.IMPORTANCE_DEFAULT;
            } else if(!isVoice && isVibrate){
                importance = NotificationManager.IMPORTANCE_LOW;
            } else {
                importance = NotificationManager.IMPORTANCE_MIN;
            }
        }else {
            importance = NotificationManager.IMPORTANCE_DEFAULT;
        }
        //解决通知声音、震动无法关闭或开启的问题
        if (TextUtils.isEmpty(CHANNEL_ID)) {
            CHANNEL_ID = "kuda_channel" + new Random().nextInt(100000);
            PreferenceUtils.setPrefString(this, PreferenceConstants.CHANNEL_ID, CHANNEL_ID);
            CHANNEL_NAME = getString(R.string.app_name) + new Random().nextInt(100000);
            PreferenceUtils.setPrefString(this, PreferenceConstants.CHANNEL_NAME, CHANNEL_NAME);
        }

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
        // 配置通知渠道的属性
        channel.setDescription(CHANNEL_NAME);
        // 设置通知出现时的闪灯（如果 android 设备支持的话）
        channel.enableLights(true);
        channel.setLightColor(Color.BLUE);
        if (isVibrate) {
            // 设置通知出现时的震动（如果 android 设备支持的话）
            channel.enableVibration(true);
            //如上设置使手机：静止1秒，震动2秒，静止1秒，震动3秒
            channel.setVibrationPattern(new long[]{1000, 500, 2000});
        } else {
            // 设置通知出现时不震动
            channel.enableVibration(false);
            channel.setVibrationPattern(new long[]{0});
        }

        if (isVoice) {
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null);
        } else {
            channel.setSound(null, null);
        }
        //把渠道添加到通知中
        manager.createNotificationChannel(channel);

        //设置跳转的页面
        PendingIntent intent = PendingIntent.getActivity(SocketService.this,
                100, new Intent(this, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(intent)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_cds_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_cds_launcher))
                .setLights(Color.BLUE, 2000, 1000)
                .setAutoCancel(true);
        if (RomUtils.isMiui()){
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
            // 关联PendingIntent
            builder.setFullScreenIntent(intent, false);// 横幅
        }
        Notification notification = builder.build();
        manager.notify(2, notification);
    }

    public void createNotification(String title, String message) {
        Logger.i(TAG, "createNotification title：" + title + " message：" + message);
        //创建通知管理类
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        checkNofificationPermission(manager);

        //设置跳转的页面
        PendingIntent intent = PendingIntent.getActivity(SocketService.this,
                100, new Intent(this, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        //创建通知建设类
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(title)//设置通知栏标题
                .setContentText(message)//设置通知栏内容
                .setContentIntent(intent)//设置跳转
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_cds_launcher)//设置图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_cds_launcher))
//                .setDefaults(Notification.DEFAULT_ALL)//设置
                .setLights(Color.BLUE, 2000, 1000)//设置提示灯
                .setAutoCancel(true);//设置自动消失

        boolean isVibrate = PreferenceUtils.getPrefBoolean(App.getInstance(), PreferenceConstants.VIBRATE_NOTIFY, true);
        if (isVibrate) {
            builder.setVibrate(new long[]{1000, 500, 2000});
        } else {
            builder.setVibrate(new long[]{0});
        }

        boolean isVoice = PreferenceUtils.getPrefBoolean(App.getInstance(), PreferenceConstants.VOICE_NOTIFY, true);
        if (isVoice) {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        } else {
            builder.setSound(null);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
            // 关联PendingIntent
            builder.setFullScreenIntent(intent, false);// 横幅
        }
        //创建通知类
        Notification notification = builder.build();
        //显示在通知栏
        manager.notify(2, notification);

    }

    public void checkNofificationPermission(NotificationManager manager) {
        boolean available = PreferenceUtils.getPrefBoolean(App.getInstance(), PreferenceConstants.SYSTEM_NOTIFY_AVAILABLE, true);
        if (!areNotificationsEnabled(manager) && available) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    new MyAlertDialog(AppManager.getInstance().getTopActivity())
                            .setTitle("开启通知")
                            .setMessage("为了避免错过重要的提醒，建议您开启系统通知。")
                            .setPositiveButton("立即开启", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                                        startActivity(intent);
                                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                        startActivity(intent);
                                    }
                                }
                            })
                            .setCancelButton("不再提示", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    PreferenceUtils.setPrefBoolean(App.getInstance(), PreferenceConstants.SYSTEM_NOTIFY_AVAILABLE, false);
                                }
                            }).show();


                }
            });
        }
    }

    public boolean areNotificationsEnabled(NotificationManager manager) {
        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return manager.areNotificationsEnabled();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AppOpsManager appOps =
                    (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = getApplicationInfo();
            String pkg = getApplicationContext().getPackageName();
            int uid = appInfo.uid;
            try {
                Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE,
                        Integer.TYPE, String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
                int value = (int) opPostNotificationValue.get(Integer.class);
                return ((int) checkOpNoThrowMethod.invoke(appOps, value, uid, pkg)
                        == AppOpsManager.MODE_ALLOWED);
            } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException
                    | InvocationTargetException | IllegalAccessException | RuntimeException e) {
                return true;
            }
        } else {
            return true;
        }
    }

    public void showExitDialog() {
        //断开tcp连接
        shutdownNetty();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new MyAlertDialog(AppManager.getInstance().getTopActivity())
                        .setTitle("下线通知")
                        .setMessage("您的账号已在另一台设备登录，如非本人操作，建议尽快修改密码")
                        .setPositiveButton("重新登录", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //清空登录信息
                                PreferenceUtils.setPrefString(App.getInstance(), PreferenceConstants.USER_PASSWORD, "");
                                PreferenceUtils.setPrefString(App.getInstance(), PreferenceConstants.USER_ID, "");
                                Intent i = new Intent().setClass(AppManager.getInstance().getTopActivity(), LoginActivity.class);
                                startActivity(i);
                                AppManager.getInstance().finishAllActivity();
                            }
                        })
                        .setCancelButton("退出", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //清空登录信息
                                PreferenceUtils.setPrefString(App.getInstance(), PreferenceConstants.USER_PASSWORD, "");
                                PreferenceUtils.setPrefString(App.getInstance(), PreferenceConstants.USER_ID, "");
                                AppManager.getInstance().appExit(App.getInstance());
                            }
                        }).show();
            }
        });
    }

    public void sendMessage(String msg) {
        Logger.w(TAG, "sendMessage：" + msg);
        if (mChannelFuture != null && mChannelFuture.channel() != null && isConnect) {
            mChannelFuture.channel().writeAndFlush(msg);
        } else {
            if (!isClose) {
                initPushService();
            }
        }
    }

    private NetworkStateReceiver mNetworkStateReceiver;

    public void registerActionReceiver() {
        Logger.d(TAG, "---------------- registerReceiver----------------");
        mNetworkStateReceiver = new NetworkStateReceiver();
        IntentFilter netIntentFilter = new IntentFilter();
        netIntentFilter.addAction(NETWORK_RECEIVER);
        registerReceiver(mNetworkStateReceiver, netIntentFilter);
    }

    /**
     * 注销广播；
     */
    public void unregisterActionReceiver() {
        Logger.d(TAG, "-------------------- unRegisterReceiver------------------");
        unregisterReceiver(mNetworkStateReceiver);
    }


    public static final String NETWORK_RECEIVER = "android.net.conn.CONNECTIVITY_CHANGE";

    /**
     * 监听系统网络状态广播，7.0 后只支持动态注册
     */
    class NetworkStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (!TextUtils.isEmpty(HEART_BEAT_STRING)) {
                    sendMessage(HEART_BEAT_STRING);
                }
            }
            Logger.i(TAG, "NetworkStateReceiver is work！");
        }
    }
}
