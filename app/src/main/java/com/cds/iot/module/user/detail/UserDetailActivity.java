package com.cds.iot.module.user.detail;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.blankj.utilcode.util.KeyboardUtils;
import com.cds.iot.App;
import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.data.Constant;
import com.cds.iot.data.entity.UserInfo;
import com.cds.iot.data.entity.WxUserInfoResp;
import com.cds.iot.module.user.changephone.ChangePhoneActivity;
import com.cds.iot.util.Logger;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.ToastUtils;
import com.cds.iot.util.picasso.PicassoCircleTransform;
import com.cds.iot.view.ActionSheetDialog;
import com.cds.iot.view.MyAlertDialog;
import com.cds.iot.wxapi.WXEntryActivity;
import com.squareup.picasso.Picasso;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;

/**
 * 用户详情页面
 */
public class UserDetailActivity extends BaseActivity implements View.OnClickListener, UserDetailContract.View {
    public static final int CHANGE_PHONE = 100;
    // 拍照回传码
    public static final int PHOTO_REQUEST_CAMERA = 101;
    // 相册选择回传吗
    public static final int PHOTO_REQUEST_GALLERY = 102;
    //裁剪程序请求吗
    public static final int PHOTO_REQUEST_CROP_PHOTO = 103;

    @Bind(R.id.head_img)
    ImageView headImg;
    @Bind(R.id.wx_isbind_tv)
    TextView wxIsbindTv;

    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
    TextView birthdayTv, sexTv, phoneTv;
    EditText nickNameEdit;

    UserDetailPresenter mPresenter;

    UserInfo mUserInfo;

    int userId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.birthday_layout).setOnClickListener(this);
        findViewById(R.id.sex_layout).setOnClickListener(this);
        findViewById(R.id.phone_layout).setOnClickListener(this);
        findViewById(R.id.wx_layout).setOnClickListener(this);
        headImg.setOnClickListener(this);
        birthdayTv = (TextView) findViewById(R.id.birthday_tv);
        sexTv = (TextView) findViewById(R.id.sex_tv);
        phoneTv = (TextView) findViewById(R.id.phone);
        nickNameEdit = (EditText) findViewById(R.id.nickname);
        nickNameEdit.setCursorVisible(false);// 内容清空后将编辑框1的光标隐藏，提升用户的体验度
        nickNameEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    nickNameEdit.setCursorVisible(true);// 再次点击显示光标
                    nickNameEdit.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.btn_modification_mine), null);
                }
                return false;
            }
        });
        nickNameEdit.setImeOptions(EditorInfo.IME_ACTION_SEND);
        nickNameEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //当actionId == XX_SEND 或者 XX_DONE时都触发
                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                        && KeyEvent.ACTION_DOWN == event.getAction())) {
                    //处理事件
                    ToastUtils.showShort(App.getInstance(), "键盘回车处理");
                    KeyboardUtils.hideSoftInput(v);
                    nickNameEdit.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.btn_savename_mine), null);
                    mUserInfo.setNickname(nickNameEdit.getText().toString());
                    mPresenter.updateUserInfo(mUserInfo);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void initData() {
        userId = Integer.parseInt(PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID, ""));
        new UserDetailPresenter(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            mUserInfo = (UserInfo) getIntent().getExtras().getSerializable("userInfo");
            setUserInfo(mUserInfo);
        } else {
            mPresenter.getUserInfo(userId);
        }
    }

    void setUserInfo(UserInfo resp) {
        sexTv.setText(resp.getSex() == 0 ? "女" : "男");
        phoneTv.setText(resp.getPhone_number());
        nickNameEdit.setCursorVisible(false);// 内容清空后将编辑框1的光标隐藏，提升用户的体验度
        nickNameEdit.setText(resp.getNickname());
        nickNameEdit.setSelection(resp.getNickname().length());

        Date date = new Date(resp.getBirthday());
        String dataStr = format.format(date);
        birthdayTv.setText(dataStr);
        wxIsbindTv.setText(resp.getWechat_state() == 0 ? "未绑定" : "已绑定");
        if (TextUtils.isEmpty(resp.getHead_img())) {
            if (resp.getSex() == 0) {
                headImg.setImageResource(R.mipmap.btn_female_small);
            } else {
                headImg.setImageResource(R.mipmap.btn_male_small);
            }
        } else {
            Picasso.with(this)
                    .load(resp.getHead_img())
                    .error(resp.getSex() == 0 ? R.mipmap.btn_female_small : R.mipmap.btn_male_small)
                    .transform(new PicassoCircleTransform())
                    .into(headImg);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.birthday_layout:
                String birthdayStr = birthdayTv.getText().toString();
                Calendar calendar = Calendar.getInstance();
                try {
                    Date date = format.parse(birthdayStr);
                    calendar.setTime(date);
                } catch (ParseException e) {
                    Logger.e(TAG, "birthday exception：" + e);
                }

                //时间选择器
                TimePickerView pvTime = new TimePickerBuilder(UserDetailActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        birthdayTv.setText(format.format(date));

                        Date currentdate = new Date(mUserInfo.getBirthday());
                        String currentDateStr = format.format(currentdate);
                        if (!birthdayTv.getText().toString().equals(currentDateStr)) {
                            mUserInfo.setBirthday(date.getTime());
                            mPresenter.updateUserInfo(mUserInfo);
                        }
                    }
                }).setOutSideCancelable(true)
                        .setDate(calendar)
                        .setSubmitText("确定")
                        .setCancelText("取消")
                        .build();
                pvTime.show();
                break;
            case R.id.sex_layout:
                final List optionList = new ArrayList();
                optionList.add("女");
                optionList.add("男");
                int selectid = "女".equals(sexTv.getText().toString()) ? 0 : 1;
                //条件选择器
                OptionsPickerView pvOptions = new OptionsPickerBuilder(UserDetailActivity.this, new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3, View v) {
                        //返回的分别是三个级别的选中位置
                        String sexStr = (String) optionList.get(options1);
                        sexTv.setText(sexStr);
                        int sex = "女".equals(sexStr) ? 0 : 1;
                        if (mUserInfo.getSex() != sex) {
                            mUserInfo.setSex("女".equals(sexStr) ? 0 : 1);
                            mPresenter.updateUserInfo(mUserInfo);
                        }
                    }
                }).setOutSideCancelable(true)
                        .setSelectOptions(selectid)  //设置默认选中项
                        .setSubmitText("确定")
                        .setCancelText("取消")
                        .build();
                pvOptions.setPicker(optionList);
                pvOptions.show();
                break;
            case R.id.phone_layout:
                Intent intent = new Intent(this, ChangePhoneActivity.class);
                intent.putExtra("phone", phoneTv.getText().toString().trim());
                startActivityForResult(intent, CHANGE_PHONE);
                break;
            case R.id.head_img:
                new ActionSheetDialog(this)
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem("拍照", ActionSheetDialog.SheetItemColor.Blue,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        openCamera();
                                    }
                                })
                        .addSheetItem("从手机相册选择", ActionSheetDialog.SheetItemColor.Blue,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        openAlbum();
                                    }
                                }).show();
                break;
            case R.id.wx_layout:
                if ("未绑定".equals(wxIsbindTv.getText().toString())) {
                    IWXAPI api = WXAPIFactory.createWXAPI(this, WXEntryActivity.WX_APP_ID, false);
                    if (!api.isWXAppInstalled()) {
                        //提醒用户没有按照微信
                        ToastUtils.showShort(this, "请先安装微信客户端");
                        return;
                    }
                    SendAuth.Req req = new SendAuth.Req();
                    req.scope = "snsapi_userinfo";
                    req.state = "wechat_login";
                    api.sendReq(req);
                    showProgressDilog();
                } else {
                    new MyAlertDialog(this)
                            .setTitle("解除绑定")
                            .setMessage("确定要解除账号与微信的关联吗？")
                            .setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mPresenter.thirdUnbind(userId + "", Constant.WX_PLATFORM_ID);
                                }
                            })
                            .setCancelButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).showDialog();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
        if (mAvatarFile != null) {
            mAvatarFile.delete();
        }
    }

    @Override
    public void setPresenter(UserDetailContract.Presenter presenter) {
        mPresenter = (UserDetailPresenter) presenter;
    }

    @Override
    public void onEventMainThread(Object event) {
        super.onEventMainThread(event);
        if (event instanceof SendAuth.Resp) {
            SendAuth.Resp resp = (SendAuth.Resp) event;
            mPresenter.getWXUserInfo(this, resp.code);
            showProgressDilog("正在获取微信用户信息...");
        }
    }

    @Override
    public void getUserInfoSuccess(UserInfo resp) {
        mUserInfo = resp;
        setUserInfo(resp);
    }

    @Override
    public void getWXUserInfoSuccess(WxUserInfoResp resp) {
        showProgressDilog("正在进行第三方绑定...");
        mPresenter.thirdPartyBind(resp.getUnionid(), Constant.WX_PLATFORM_ID, userId + "");
    }

    @Override
    public void getWXUserInfoFailed() {
        hideProgressDilog();
    }

    @Override
    public void updateUserInfoSuccess() {
        ToastUtils.showShort(this, "用户信息修改成功");
        mPresenter.getUserInfo(userId);
        setResult(RESULT_OK);
    }

    @Override
    public void thirdUnbindSuccess() {
        ToastUtils.showShort(this, "解绑成功");
        mPresenter.getUserInfo(userId);
        setResult(RESULT_OK);
    }

    @Override
    public void thirdPartyBindSuccess() {
        hideProgressDilog();
        ToastUtils.showShort(this, "绑定成功");
        mPresenter.getUserInfo(userId);
        setResult(RESULT_OK);
    }

    @Override
    public void thirdPartyBindFailed() {
        hideProgressDilog();
    }

    private String mPhotoPath;
    private File mPhotoFile;
    private File mAvatarFile;
    private Uri imageUri;

    /**
     * 调用手机相机
     */
    private void openCamera() {
        mPhotoPath = App.getInstance().getAppCacheDir() + UUID.randomUUID().toString() + ".jpg";
        mPhotoFile = new File(mPhotoPath);
        Intent intentCamera = new Intent();
        getFileUri(mPhotoFile);
        intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intentCamera, PHOTO_REQUEST_CAMERA);
    }

    /**
     * 调用手机相册
     */
    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.i(TAG, "onActivityResult requestCode：" + requestCode + "，resultCode：" + resultCode);
        if (requestCode == CHANGE_PHONE && resultCode == RESULT_OK) {
            mPresenter.getUserInfo(userId);
        }
        if (requestCode == PHOTO_REQUEST_GALLERY && resultCode == RESULT_OK) {
            //调用相册
            Cursor cursor = getContentResolver().query(data.getData(), new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            //游标移到第一位，即从第一位开始读取
            if (cursor != null) {
                cursor.moveToFirst();
                mPhotoPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();
                //调用系统裁剪
                mPhotoFile = new File(mPhotoPath);
                getFileUri(mPhotoFile);
                startPhoneZoom(imageUri);
            }
        }

        if (requestCode == PHOTO_REQUEST_CAMERA && resultCode == RESULT_OK) {
            //相机返回结果，调用系统裁剪
            startPhoneZoom(imageUri);
        }
        if (requestCode == PHOTO_REQUEST_CROP_PHOTO && resultCode == RESULT_OK) {
            Picasso.with(this)
                    .load(mAvatarFile)
                    .transform(new PicassoCircleTransform())
                    .into(headImg);

            mUserInfo.setHead_img(mAvatarFile.getAbsolutePath());
            mPresenter.updateUserInfo(mUserInfo);
        }
    }

    private Uri getFileUri(File mAvatarFile) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(this, "com.cds.iot.fileprovider", mAvatarFile);
        } else {
            imageUri = Uri.fromFile(mAvatarFile);
        }
        return imageUri;
    }

    /**
     * 调用系统裁剪的方法
     */
    private void startPhoneZoom(Uri uri) {
        mAvatarFile = new File(App.getInstance().getAppCacheDir(), UUID.randomUUID().toString() + ".jpg");//这个是创建一个截取后的图片路径和名称。
        try {
            if (mAvatarFile.exists()) {
                mAvatarFile.delete();
            }
            mAvatarFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageUri = Uri.fromFile(mAvatarFile);

        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        //是否可裁剪
        intent.putExtra("corp", "true");
        //是否压缩
        intent.putExtra("scale", true);
        //裁剪器高宽比
        intent.putExtra("aspectY", 1);
        intent.putExtra("aspectX", 1);
        //设置裁剪框高宽
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);

        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, PHOTO_REQUEST_CROP_PHOTO);
    }
}
