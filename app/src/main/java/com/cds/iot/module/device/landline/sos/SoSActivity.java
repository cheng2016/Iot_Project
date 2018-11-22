package com.cds.iot.module.device.landline.sos;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cds.iot.App;
import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.data.Constant;
import com.cds.iot.data.entity.ContactBean;
import com.cds.iot.data.entity.SaveTelphoneReq;
import com.cds.iot.data.entity.TelphoneInfo;
import com.cds.iot.module.adapter.ContactAdapter;
import com.cds.iot.util.Logger;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.ToastUtils;
import com.cds.iot.util.Utils;
import com.cds.iot.view.EditTextDialog;
import com.cds.iot.view.MyAlertDialog;
import com.cds.iot.view.PhoneAlertDialog;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;

/**
 * @author Chengzj
 * @date 2018/10/9 16:49
 * <p>
 * SOS设置界面
 */
public class SoSActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, SosContract.View {
    private static final int DELETE_RECORD = 100;

    private static final int DELETE_CONTACT = 200;

    private static final int SOS_MAX_LENGTH = 70;
    @Bind(R.id.contact_title)
    TextView contactTitle;
    @Bind(R.id.listview)
    ListView listView;
    @Bind(R.id.sosMsg)
    EditText sosMsgEdit;
    @Bind(R.id.rescue_call_tv)
    TextView rescueCallTv;
    @Bind(R.id.edit_status)
    TextView editStatus;
    @Bind(R.id.voice_tip)
    AppCompatImageView voiceTip;
    @Bind(R.id.voice_anim)
    AppCompatImageView voiceAnim;
    @Bind(R.id.length_tv)
    TextView lengthTv;
    @Bind(R.id.voice_record_btn)
    TextView voiceRecordBtn;
    @Bind(R.id.voice_replace_btn)
    AppCompatImageView voiceReplaceBtn;
    @Bind(R.id.voice_layout)
    RelativeLayout voiceLayout;
    @Bind(R.id.synchronization_btn)
    RelativeLayout synchronizationBtn;
    @Bind(R.id.synchronization_tv)
    AppCompatTextView synchronizationTv;

    SosContract.Presenter mPresenter;

    ContactAdapter mAdapter;

    EditTextDialog mEditTextDialog;
    MyAlertDialog mAlertDialog;


    private String deviceId;
    private String userId;

    private boolean hasLocalVoice = false;
    private boolean isPlay = false;
    private MediaPlayer player;
    private AnimationDrawable animationDrawable;
    //录音地址
    private String tempFilePath = App.getInstance().getAppCacheDir() + "temp_media.wav";
    private String filePath = App.getInstance().getAppCacheDir() + "telephone_media.wav";
    //网络录音地址
    private String file_url;

    private boolean isAdmin;

    @Override
    public int getLayoutId() {
        return R.layout.activity_sos;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((TextView) findViewById(R.id.title)).setText("SOS设置");
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.rescue_call).setOnClickListener(this);
        voiceAnim.setOnClickListener(this);
        synchronizationBtn.setOnClickListener(this);

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
                        + SOS_MAX_LENGTH);
            }
        });
    }

    @Override
    protected void initData() {
        new SosPresenter(this);
        mAdapter = new ContactAdapter(this);
        mAdapter.setAddOnClickListener(new ContactAdapter.AddOnClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showChooseDialog(position);
            }

            @Override
            public boolean onLongClick(View view, final int position) {
                showDeleteDialog("确认删除此联系人？", DELETE_CONTACT, position);
                return false;
            }
        });
        listView.setAdapter(mAdapter);

        if (getIntent() != null && getIntent().getExtras() != null) {
            userId = PreferenceUtils.getPrefString(this, PreferenceConstants.USER_ID, "");
            deviceId = getIntent().getStringExtra("deviceId");
            mPresenter.getTelphoneInfo(deviceId);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    void showChooseDialog(final int index) {
        if (mAdapter.getDataList().size() != index) {
            mEditTextDialog = new EditTextDialog(this, mAdapter.getDataList().get(index).getName(), mAdapter.getDataList().get(index).getPhone());
        } else {
            mEditTextDialog = new EditTextDialog(this);
        }
        mEditTextDialog.setPositiveButton(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mEditTextDialog.getNameText()) || TextUtils.isEmpty(mEditTextDialog.getPhoneText())) {
                    ToastUtils.showShort(SoSActivity.this, "姓名或电话不能为空！");
                    return;
                }
                List<ContactBean> listItems = mAdapter.getDataList();
                if (index == listItems.size()) {
                    if (listItems != null && listItems.size() > 0) {
                        for (ContactBean bean : listItems) {
                            if (bean.getPhone().equals(mEditTextDialog.getPhoneText().trim())) {
                                Toast.makeText(SoSActivity.this, "该电话已存在，请输入新的号码！", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                    listItems.add(new ContactBean(mEditTextDialog.getNameText(), mEditTextDialog.getPhoneText()));
                    mAdapter.setDataList(listItems);
                    contactTitle.setText(String.format(getString(R.string.contact_size), listItems.size()));
                } else {
                    if (!listItems.get(index).getPhone().equals(mEditTextDialog.getPhoneText().trim())) {
                        for (ContactBean bean : listItems) {
                            if (bean.getPhone().equals(mEditTextDialog.getPhoneText().trim())) {
                                Toast.makeText(SoSActivity.this, "该电话已存在，请输入新的号码！", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                    listItems.get(index).setName(mEditTextDialog.getNameText());
                    listItems.get(index).setPhone(mEditTextDialog.getPhoneText().trim());
                    mAdapter.setDataList(listItems);
                    contactTitle.setText(String.format(getString(R.string.contact_size), listItems.size()));
                }
                mEditTextDialog.dismiss();
            }
        }).setContactButton(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openContact();
            }
        }).showDialog();
    }

    void showDeleteDialog(String message, final int type, final int position) {
        mAlertDialog = new MyAlertDialog(this, R.style.customDialog)
                .setTitle(message)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (type == DELETE_RECORD) {
                            voiceRecordBtn.setVisibility(View.VISIBLE);

                        } else if (type == DELETE_CONTACT) {
                            mAdapter.getDataList().remove(position);
                            mAdapter.setDataList(mAdapter.getDataList());
                            Utils.setListViewHeightBasedOnChildren(listView);
                            contactTitle.setText(String.format(getString(R.string.contact_size), mAdapter.getDataList().size()));
                        }
                    }
                }).showDialog();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.rescue_call:
                new PhoneAlertDialog(this, rescueCallTv.getText().toString())
                        .setTitle("选择报警电话")
                        .setPositiveButton("确定", new PhoneAlertDialog.OnPhoneClickListener() {
                            @Override
                            public void onClick(View var1, int index) {
                                String[] strings = {"110", "120", "119"};
                                rescueCallTv.setText(strings[index]);
                            }
                        }).showDialog();
                break;
            case R.id.voice_anim:
                startPlay();
                break;
            case R.id.synchronization_btn:
                saveTelphoneInfo();
                break;
            default:
                break;
        }
    }

    void showReplaceDialog(SaveTelphoneReq req) {
        mAlertDialog = new MyAlertDialog(this)
                .setTitle("提示")
                .setMessage("录音文件已更改，是否上传？")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPresenter.saveTelphoneInfo(req, filePath);
                    }
                }).showDialog();
    }


    void saveTelphoneInfo() {
        if (mAdapter.getDataList() == null || mAdapter.getDataList().size() == 0) {
            ToastUtils.showShort(this, "请添加紧急联系人");
            return;
        }
        if (TextUtils.isEmpty(sosMsgEdit.getText().toString())) {
            ToastUtils.showShort(this, "请输入紧急群发短信内容");
            return;
        }
        SaveTelphoneReq req = new SaveTelphoneReq();
        req.setDevice_id(deviceId);
        req.setUser_id(userId);
        req.setMessage(sosMsgEdit.getText().toString());
        req.setAlarm_type(rescueCallTv.getText().toString());
        req.setRecord_is_modify(hasLocalVoice ? 1 : 0);
        req.setContact(mAdapter.getDataList());
        if (hasLocalVoice) {
            if (TextUtils.isEmpty(file_url)) {
                mPresenter.saveTelphoneInfo(req, filePath);
            } else {
                showReplaceDialog(req);
            }
        } else {
            mPresenter.saveTelphoneInfo(req, "");
        }
    }

    @Override
    public void getTelphoneInfoSuccess(TelphoneInfo resp) {
        rescueCallTv.setText(resp.getAlarm_type());
        if (TextUtils.isEmpty(resp.getFile_url())) {
            file_url = null;
            voiceRecordBtn.setVisibility(View.VISIBLE);
            voiceLayout.setVisibility(View.GONE);
        } else {
            file_url = resp.getFile_url();
            voiceRecordBtn.setVisibility(View.GONE);
            voiceLayout.setVisibility(View.VISIBLE);
            voiceTip.setVisibility(View.GONE);
        }
        sosMsgEdit.setText(resp.getMessage());
        lengthTv.setText(resp.getRecord_duration());
        if (resp.getContact() != null && resp.getContact().size() > 0) {
            mAdapter.setDataList(resp.getContact());
        }
        isAdmin = Constant.ALARM_ADMIN.equals(resp.getIs_admin()) ? true : false;
        if (isAdmin) {
            synchronizationBtn.setClickable(true);
            synchronizationBtn.setBackground(getResources().getDrawable(R.drawable.button_float_selector));
            synchronizationTv.setText("同步至座机");
            synchronizationTv.setTextColor(getResources().getColor(R.color.white));
            synchronizationTv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.icn_synctotel), null, null, null);
        } else {
            synchronizationBtn.setClickable(false);
            synchronizationBtn.setBackgroundColor(getResources().getColor(R.color.white));
            synchronizationTv.setText("仅限管理员设置");
            synchronizationTv.setTextColor(getResources().getColor(R.color.enable_color));
            synchronizationTv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    @Override
    public void saveTelphoneInfoSuccess() {
        ToastUtils.showShort(this,"数据提交成功，请五分钟后检查您的座机！");
        hasLocalVoice = false;
        voiceTip.setVisibility(View.GONE);
        sosMsgEdit.setCursorVisible(false);// 内容清空后将编辑框1的光标隐藏，提升用户的体验度
        mPresenter.getTelphoneInfo(deviceId);
    }

    @Override
    public void saveTelphoneInfoFail() {

    }

    void startPlay(){
        if (isPlay) {
            releaseMediaPlayer();
            return;
        }
        isPlay = true;
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        player.setOnPreparedListener(this);
        player.setOnErrorListener(this);
        try {
            if (hasLocalVoice) {
                player.setDataSource(filePath);
            } else {
                player.setDataSource(file_url);
            }
            player.prepareAsync();
        } catch (IOException e) {
            Logger.e(TAG, "playMedia Exception：" + e);
        }
        voiceAnim.setBackgroundResource(R.drawable.anim_voice);
        animationDrawable = (AnimationDrawable) voiceAnim.getBackground();
        if (animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }

    void releaseMediaPlayer() {
        if (player != null && player.isPlaying()) {
            player.pause();
            player.stop();
            player.release();
            player = null;
            if (animationDrawable != null && animationDrawable.isRunning()) {
                animationDrawable.stop();
                voiceAnim.setBackgroundResource(R.mipmap.btn_sossound3);
            }
            isPlay = false;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        animationDrawable.start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        isPlay = false;
        animationDrawable.stop();
        if (voiceAnim != null) {
            voiceAnim.setBackgroundResource(R.mipmap.btn_sossound3);
        }
        player = null;
    }


    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        releaseMediaPlayer();
        isPlay = false;
        player = null;
        return false;
    }

    //  按钮的点击事件： 打开系统联系人。
    private void openContact() {
        //检查是否有读取联系人权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                ToastUtils.showShort(this, "请检查是否有读取联系人权限！");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 666);
                return;
            }
        }
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("vnd.android.cursor.dir/phone_v2");
        startActivityForResult(intent, 1);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 666) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Logger.i(TAG, "申请读取联系人权限成功");
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setType("vnd.android.cursor.dir/phone_v2");
                startActivityForResult(intent, 1);
            } else {
                Logger.i(TAG, "申请读取联系人权限失败");
                ToastUtils.showShort(SoSActivity.this, "请检查是否有读取联系人权限！");
            }
        }
        if (requestCode == 888) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                initRecord();
                Logger.i(TAG, "申请录音权限成功");
            } else {
                Logger.i(TAG, "申请录音权限权限失败");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri uri = data.getData();
            String[] contact = getPhoneContacts(uri);
            if (contact != null) {
                String name = contact[0];//姓名
                String number = contact[1];//手机号
                number = formatPhoneNum(number);
                mEditTextDialog.setNameEdit(name);
                mEditTextDialog.setPhoneEdit(number);
            }
        }
    }

    /**
     * 去掉手机号内除数字外的所有字符
     *
     * @param phoneNum 手机号
     * @return
     */
    private String formatPhoneNum(String phoneNum) {
        String regex = "(\\+86)|[^0-9]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNum);
        return matcher.replaceAll("");
    }

    /**
     * 读取联系人信息
     *
     * @param uri
     */
    private String[] getPhoneContacts(Uri uri) {
        String[] contact = new String[2];
        //得到ContentResolver对象
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            //取得联系人姓名
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            contact[0] = cursor.getString(nameFieldColumnIndex);
            contact[1] = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Log.i("contacts", contact[0]);
            Log.i("contactsUsername", contact[1]);
            cursor.close();
        }
        return contact;
    }


    @Override
    public void setPresenter(SosContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //触摸的是EditText并且当前EditText可以滚动则将事件交给EditText处理；否则将事件交由其父类处理
        if ((view.getId() == R.id.sosMsg && canVerticalScroll(sosMsgEdit))) {
            view.getParent().requestDisallowInterceptTouchEvent(true);
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view.getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return false;
    }

    /**
     * EditText竖直方向是否可以滚动
     *
     * @param editText 需要判断的EditText
     * @return true：可以滚动   false：不可以滚动
     */
    private boolean canVerticalScroll(EditText editText) {
        //滚动的距离
        int scrollY = editText.getScrollY();
        //控件内容的总高度
        int scrollRange = editText.getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() - editText.getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        int scrollDifference = scrollRange - scrollExtent;

        if (scrollDifference == 0) {
            return false;
        }
        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }
}
