package com.cds.iot.data.entity;

/**
 * @author Chengzj
 * @date 2018/9/27 9:54
 */
public class ThirdBindReq {

    /**
     * user_id : 用户id
     * platform_id : 三方平台的唯一标识
     * login_type : 登录类型，0:微信登录
     * phone_number : 手机号码
     * login_pwd : 密码
     * code : 获取的验证码
     * nickname : 平台昵称
     * head_img : 头像链接
     */

    private String user_id;
    private String platform_id;
    private int login_type;

    private String phone_number;
    private String login_pwd;
    private String code;
    private String nickname;
    private String head_img;



    /**
     * 第三方登录参数
     * @param platform_id 三方平台的唯一标识
     * @param login_type 登录类型，0:微信登录
     */
    public ThirdBindReq(String platform_id, int login_type) {
        this.platform_id = platform_id;
        this.login_type = login_type;
    }

    /**
     * 已注册用户第三方绑定参数
     * @param user_id 用户id
     * @param platform_id 三方平台的唯一标识
     * @param login_type 登录类型，0:微信登录
     */
    public ThirdBindReq(String user_id, String platform_id, int login_type) {
        this.user_id = user_id;
        this.platform_id = platform_id;
        this.login_type = login_type;
    }

    /**
     * 未注册第三方绑定参数
     *
     * platform_id : 三方平台的唯一标识
     * login_type : 登录类型，0:微信登录
     * phone_number : 手机号码
     * login_pwd : 密码
     * code : 获取的验证码
     * nickname : 平台昵称
     * head_img : 头像链接
     */
    public ThirdBindReq(String platform_id, int login_type, String phone_number, String login_pwd, String code, String nickname, String head_img) {
        this.platform_id = platform_id;
        this.login_type = login_type;
        this.phone_number = phone_number;
        this.login_pwd = login_pwd;
        this.code = code;
        this.nickname = nickname;
        this.head_img = head_img;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPlatform_id() {
        return platform_id;
    }

    public void setPlatform_id(String platform_id) {
        this.platform_id = platform_id;
    }

    public int getLogin_type() {
        return login_type;
    }

    public void setLogin_type(int login_type) {
        this.login_type = login_type;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getLogin_pwd() {
        return login_pwd;
    }

    public void setLogin_pwd(String login_pwd) {
        this.login_pwd = login_pwd;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHead_img() {
        return head_img;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }
}
