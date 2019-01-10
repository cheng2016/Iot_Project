package com.cds.iot.module.feedback;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cds.iot.App;
import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.util.ToastUtils;
import com.cds.iot.view.ActionSheetDialog;
import com.cds.iot.view.ActionSheetDialog.OnSheetItemClickListener;
import com.cds.iot.view.ActionSheetDialog.SheetItemColor;
import com.cds.iot.view.CustomDialog;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import butterknife.Bind;

public class FeedBackActivity extends BaseActivity implements View.OnClickListener, FeedBackContract.View {
    // 拍照回传码
    public final static int PHOTO_REQUEST_CAMERA = 100;
    // 相册选择回传吗
    public final static int PHOTO_REQUEST_GALLERY = 200;
    //裁剪程序请求吗
    public static final int PHOTO_REQUEST_CROP_PHOTO = 300;

    private static final int MESSAGE_MAX_LENGTH = 200;

    private ImageView feedbackImg, delImg;

    @Bind(R.id.sosMsg)
    EditText sosMsgEdit;
    @Bind(R.id.edit_status)
    TextView editStatus;

    private String mFilePath = "";

    private FeedBackPresenter mPresenter;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
        if (mAvatarFile != null) {
            mAvatarFile.delete();
            mFilePath = "";
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.commit_btn).setOnClickListener(this);
        feedbackImg = (ImageView) findViewById(R.id.feedback_img);
        delImg = (ImageView) findViewById(R.id.del_img);
        delImg.setOnClickListener(this);
        feedbackImg.setOnClickListener(this);
        sosMsgEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = sosMsgEdit.getText().toString();
                editStatus.setText(content.length() + "/"
                        + MESSAGE_MAX_LENGTH);
            }
        });
    }

    @Override
    protected void initData() {
        new FeedBackPresenter(this);
        ((TextView) findViewById(R.id.title)).setText("意见反馈");
        //第二个参数是需要申请的权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {   //权限还没有授予，需要在这里写申请权限的代码
            // 第二个参数是一个字符串数组，里面是你需要申请的权限 可以设置申请多个权限
            // 最后一个参数是标志你这次申请的权限，该常量在onRequestPermissionsResult中使用到
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PHOTO_REQUEST_CAMERA);

        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.feedback_img:
                new ActionSheetDialog(this)
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem("拍照", SheetItemColor.Blue,
                                new OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        openCamera();
                                    }
                                })
                        .addSheetItem("从手机相册选择", SheetItemColor.Blue,
                                new OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        openAlbum();
                                    }
                                }).show();
                break;
            case R.id.commit_btn:
                String content = sosMsgEdit.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.showShort(App.getInstance(), "请先填写反馈内容");
                } else if(content.length() <20){
                    ToastUtils.showShort(App.getInstance(), "请填写不低于20个字的内容描述");
                }else {
                    showProgressDilog();
                    mPresenter.feedback(content, mFilePath);
                }
                break;
            case R.id.del_img:
                new CustomDialog(this)
                        .setMessage("确认删除图片？")
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                feedbackImg.setImageResource(R.mipmap.btn_addimage);
                                delImg.setVisibility(View.GONE);
                                mFilePath = "";
                            }
                        }).showDialog();
                break;
            default:
                break;
        }
    }

    @Override
    public void setPresenter(FeedBackContract.Presenter presenter) {
        mPresenter = (FeedBackPresenter) presenter;
    }

    @Override
    public void feedbackSuccess() {
        ToastUtils.showShort(App.getInstance(), "意见反馈成功");
        finish();
    }

    @Override
    public void feedbackFailed() {
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
        //调用相册，返回结果
        if (requestCode == PHOTO_REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
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

        //调用相机,返回结果
        if (requestCode == PHOTO_REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            startPhoneZoom(imageUri);
        }
        //调用系统裁剪，返回结果
        if (requestCode == PHOTO_REQUEST_CROP_PHOTO && resultCode == Activity.RESULT_OK) {
            Picasso.with(this)
                    .load(mAvatarFile)
                    .into(feedbackImg);
            mFilePath = mAvatarFile.getAbsolutePath();
            delImg.setVisibility(View.VISIBLE);
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
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        //是否可裁剪
        intent.putExtra("corp", "true");
        //是否压缩
        intent.putExtra("scale", true);
        //裁剪器高宽比
        if (android.os.Build.MODEL.contains("HUAWEI")
                || Build.MANUFACTURER.equals("HUAWEI")) {//华为机特殊处理，防止裁剪框为圆形
            intent.putExtra("aspectX", 9998);
            intent.putExtra("aspectY", 9999);
        } else {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
        }
        //设置裁剪框高宽
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        //返回数据
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, PHOTO_REQUEST_CROP_PHOTO);
    }
}
