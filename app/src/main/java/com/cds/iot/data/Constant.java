package com.cds.iot.data;

/**
 * App 常数变量
 */
public interface Constant {
    String SCENES_DEFAULT = "0";

    String SCENES_USER = "1";

    String OS_IS_DEBUG = "os_is_debug";

    String HTTP_URL = "url";

    int REQUEST_WX_LOGIN = 101;

    int CHANGE_PHONE = 101;

    int WX_PLATFORM_ID = 0;


    int FENCE_TYPE_HOME = 1;

    int FENCE_TYPE_COMPANY = 2;

    // 拍照回传码
    int PHOTO_REQUEST_CAMERA = 100;
    // 相册选择回传吗
    int PHOTO_REQUEST_GALLERY = 200;
    //裁剪程序请求吗
    int PHOTO_REQUEST_CROP_PHOTO = 300;


    String DEVICE_STATE_AUDIT = "0";

    String DEVICE_STATE_BIND = "4";

    String DEVICE_IS_ADMIN = "1";

    /**
     * 设备类型id
     */
//    String DEVICE_TYPE_WATCH = "2";
//
//    String DEVICE_TYPE_TELEPHONE = "3";
//
//    String DEVICE_TYPE_CAR_ANALYZER = "4";
//
//    String DEVICE_TYPE_CAR_MIRROR = "1";

    int DEVICE_TYPE_WATCH = 2;

    int DEVICE_TYPE_TELEPHONE = 3;

    int DEVICE_TYPE_CAR_ANALYZER = 4;

    int DEVICE_TYPE_CAR_MIRROR = 1;

    /**
     * 处理设备指令
     */
    String MANAGE_RELEASE = "0";
    String MANAGE_AGREE = "1";
    String MANAGE_REFUSE = "2";
    String MANAGE_REVOKE = "3";

    /**
     * 消息设备状态
     */
    String DEVICE_WAIT_AGREE = "1";
    //管理员拒绝添加
    String DEVICE_REFUSE = "2";
    //用户撤销申请
    String DEVICE_REVOKE = "3";
    //添加成功
    String DEVICE_INSERT_SUCCESS = "4";

    int DEVICE_STATE_WAIT_AGREE = 1;

    int DEVICE_STATE_REFUSE = 2;

    int DEVICE_STATE_REVOKE = 3;

    int DEVICE_STATE_INSERT_SUCCESS = 4;

    /**
     * 是否在线
     */
    String DEVICE_ONLINE = "1";


    /**
     * 是否是管理员
     */
    String IS_ADMIN = "1";
    /**
     * 消息类型
     */
    //用户设备添加申请
    int MESSAGE_TYPE_USER_ADD_APPLY = 30;
    //用户设备添加申请被拒绝
    int MESSAGE_TYPE_USER_ADD_APPLY_REFUSE = 31;
    //用户设备添加申请被同意
    int MESSAGE_TYPE_USER_ADD_APPLY_AGREE = 32;
    //管理员解绑用户的设备
    int MESSAGE_TYPE_ADMIN_REMOVE_USER_DEVICE = 33;
    //用户解绑设备
    int MESSAGE_TYPE_USER_REMOVE_DEVICE = 34;
    //管理员解绑自己
    int MESSAGE_TYPE_ADMIN_REMOVE_DEVICE = 35;
    //管理员更换，新管理员收到消息
    int MESSAGE_TYPE_ADMIN_REPLACE = 36;
    //管理员更换,用户收到数据
    int MESSAGE_TYPE_ADMIN_REPLACE_NOTIFY_USER = 37;

    //照片、视频回传
    int MESSAGE_TYPE_DEVICE_IMAGE = 51;
    int MESSAGE_TYPE_DEVICE_VIDEO = 52;

    //进出区域报警
    int MESSAGE_TYPE_IN_REGION_ALARM = 10;
    int MESSAGE_TYPE_OUT_REGION_ALARM = 11;

    //汽车碰撞
    int MESSAGE_TYPE_CAR_BUMP = 20;
    //低电报警
    int MESSAGE_TYPE_LOW_ELECTRIC_ALARM = 25;

    //闹钟：是否是管理员 0：否，1：是
    String ALARM_ADMIN = "1";
}
