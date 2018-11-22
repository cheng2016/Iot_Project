package com.cds.iot.module.device.landline.wireless;

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
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cds.iot.App;
import com.cds.iot.R;
import com.cds.iot.base.BaseFragment;
import com.cds.iot.data.Constant;
import com.cds.iot.data.entity.ContactBean;
import com.cds.iot.data.entity.SaveTelphoneReq;
import com.cds.iot.data.entity.TelphoneInfo;
import com.cds.iot.module.adapter.ContactAdapter;
import com.cds.iot.module.device.landline.record.AuditRecorderConfiguration;
import com.cds.iot.module.device.landline.record.ExtAudioRecorder;
import com.cds.iot.module.device.landline.record.FailRecorder;
import com.cds.iot.util.DeviceUtils;
import com.cds.iot.util.FileUtils;
import com.cds.iot.util.Logger;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.ResourceUtils;
import com.cds.iot.util.ToastUtils;
import com.cds.iot.util.Utils;
import com.cds.iot.view.EditTextDialog;
import com.cds.iot.view.MyAlertDialog;
import com.cds.iot.view.PhoneAlertDialog;
import com.cds.iot.view.RecordHintDialog;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnClick;

public class TelephoneFragment extends BaseFragment implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, View.OnTouchListener, MediaPlayer.OnErrorListener, TelephoneContract.View {
    private static final int DELETE_RECORD = 100;

    private static final int DELETE_CONTACT = 200;

    private static final int SOS_MAX_LENGTH = 70;

    @Bind(R.id.head_img)
    ImageView headImg;
    @Bind(R.id.name_tv)
    EditText nameTv;
    @Bind(R.id.synchronization_btn)
    Button synchronizationBtn;
    @Bind(R.id.main_layout)
    LinearLayout mainLayout;
    @Bind(R.id.listview)
    ListView listView;
    @Bind(R.id.record_btn)
    ImageView recordBtn;
    @Bind(R.id.contact_title)
    TextView contactTitle;
    @Bind(R.id.sosMsg)
    EditText sosMsg;
    @Bind(R.id.voice_anim)
    ImageView voiceAnim;
    @Bind(R.id.voice_layout)
    FrameLayout voiceLayout;
    @Bind(R.id.voice_tip)
    ImageView voiceTip;
    @Bind(R.id.voice_length)
    TextView voiceLength;

    @Bind(R.id.record_big_btn)
    TextView recordBigBtn;
    @Bind(R.id.record_layout)
    RelativeLayout recordLayout;
    @Bind(R.id.call_btn)
    TextView callBtn;
    @Bind(R.id.edit_status)
    TextView editStatus;

    private String deviceId = "";

    private MediaPlayer player;

    private RecordHintDialog recordHintDialog;

    private int touchSlop;

    private ContactAdapter mAdapter;

    // 获取类的实例
    private ExtAudioRecorder recorder;
    //录音地址
    private String tempFilePath = Environment.getExternalStorageDirectory() + File.separator + "temp_media.wav";
    private String filePath = Environment.getExternalStorageDirectory() + File.separator + "telephone_media.wav";

    private TelphoneInfo mMsgSosResp;

    private AnimationDrawable animationDrawable;

    private MyAlertDialog mAlertDialog;

    private EditTextDialog mEditTextDialog;

    private boolean hasLocalVoice = false;

    private int isModify = 0;

    private boolean isPlay = false;

    private TelephoneContract.Presenter mPresenter;

    @Override
    public void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
    }

    public static TelephoneFragment newInstance() {
        return new TelephoneFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_telephone;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        nameTv.setCursorVisible(false);// 内容清空后将编辑框1的光标隐藏，提升用户的体验度
        nameTv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    nameTv.setCursorVisible(true);// 再次点击显示光标
                }
                return false;
            }
        });
        mAdapter = new ContactAdapter(getActivity());
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
        voiceLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                view.setTag("voice");
                showDeleteDialog("确认删除此段录音吗？", DELETE_RECORD, 0);
                return false;
            }
        });

        sosMsg.setOnTouchListener(this);
        InputFilter[] nameFilters = {new InputFilter.LengthFilter(15), emojiFilter};
        InputFilter[] sosFilters = {new InputFilter.LengthFilter(SOS_MAX_LENGTH), emojiFilter};
        sosMsg.setFilters(sosFilters);
        nameTv.setFilters(nameFilters);

        sosMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                String content = sosMsg.getText().toString();
                editStatus.setText(content.length() + "/"
                        + SOS_MAX_LENGTH);
            }
        });
        recordHintDialog = new RecordHintDialog(getActivity());
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        recordBtn.setOnTouchListener(new PressToSpeakListen());
        recordBigBtn.setOnTouchListener(new PressToSpeakListen());
        initRecord();
    }

    @Override
    protected void initData() {
        new TelephonePresenter(this);
        if (getArguments() != null) {
            deviceId = getArguments().getString("deviceId");
            mPresenter.getTelphoneInfo(deviceId);
        }
    }


    InputFilter emojiFilter = new InputFilter() {
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher emojiMatcher = emoji.matcher(source);
            if (emojiMatcher.find() || source.toString().matches(".*\\p{So}.*")) {
                ToastUtils.showShort(getActivity(), "不支持输入表情");
                return "";
            }
            return null;
        }
    };

    void initRecord() {
        AuditRecorderConfiguration configuration = new AuditRecorderConfiguration.Builder()
                .recorderListener(listener)
                .handler(handler)
                .uncompressed(true)
                .builder();
        recorder = new ExtAudioRecorder(configuration);
    }

    /**
     * 设置Dialog的图片
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            recordHintDialog.setImage(msg.what);
        }
    };

    /**
     * 录音失败的提示
     */
    ExtAudioRecorder.RecorderListener listener = new ExtAudioRecorder.RecorderListener() {
        @Override
        public void recordFailed(FailRecorder failRecorder) {
            if (failRecorder.getType() == FailRecorder.FailType.NO_PERMISSION) {
//                ToastUtils.showShort(getActivity(), "录音失败，可能是没有给权限");
                Logger.e(TAG, "未获取录音权限！");
            } else {
                Logger.e(TAG, "发生了未知错误！");
            }
        }
    };


    @OnClick({R.id.name_tv, R.id.synchronization_btn, R.id.voice_layout, R.id.call_btn, R.id.rescue_call})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.name_tv:
                break;
            case R.id.synchronization_btn:
                saveSosMsg();
                break;
            case R.id.voice_layout:
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
                        player.setDataSource(ResourceUtils.getProperties(App.getInstance(), Constant.HTTP_URL) + mMsgSosResp.getFile_url());
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
                break;
            case R.id.rescue_call:
                PhoneAlertDialog phoneAlertDialog = new PhoneAlertDialog(getActivity(), callBtn.getText().toString().substring(5, 8))
                        .setTitle("选择报警电话")
                        .setPositiveButton("确定", new PhoneAlertDialog.OnPhoneClickListener() {
                            @Override
                            public void onClick(View var1, int index) {
                                String[] strings = {"110", "120", "119"};
                                callBtn.setText("报警电话（" + strings[index] + "）");
                            }
                        });
                phoneAlertDialog.show();
                break;
            default:
                break;
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
                voiceAnim.setBackgroundResource(R.mipmap.icn_audio3);
            }
            isPlay = false;
        }
    }

    void saveSosMsg() {
        if (TextUtils.isEmpty(nameTv.getText().toString())) {
            ToastUtils.showShort(getActivity(), "座机名不能为空！");
            return;
        }
        if (TextUtils.isEmpty(sosMsg.getText().toString())) {
            ToastUtils.showShort(getActivity(), "短信内容不能为空！");
            return;
        }
        String userId = PreferenceUtils.getPrefString(getActivity(), PreferenceConstants.USER_ID, "");
        SaveTelphoneReq requst = new SaveTelphoneReq();
        requst.setUser_id(userId);
        requst.setDevice_id(deviceId);
//        requst.setNickname(nameTv.getText().toString());
        requst.setContact(mAdapter.getDataList());
        requst.setMessage(sosMsg.getText().toString());
        requst.setAlarm_type(callBtn.getText().toString().substring(5, 8));
        if (hasLocalVoice) {
            if (TextUtils.isEmpty(mMsgSosResp.getFile_url())) {
                mPresenter.saveTelphoneInfo(requst, filePath);
            } else {
                showReplaceDialog(requst);
            }
        } else {
            mPresenter.saveTelphoneInfo(requst, "");
        }
        Log.i(TAG, "filePath：" + filePath);
        Logger.i(TAG, "saveSosMsg：" + new Gson().toJson(requst));
    }

    void showReplaceDialog(final SaveTelphoneReq requst) {
        mAlertDialog = new MyAlertDialog(getActivity())
                .setTitle("提示")
                .setMessage("录音文件已更改，是否上传？")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPresenter.saveTelphoneInfo(requst, filePath);
                    }
                });
        mAlertDialog.show();
    }

    void showDeleteDialog(String message, final int type, final int position) {
        mAlertDialog = new MyAlertDialog(getActivity(), R.style.customDialog)
                .setTitle(message)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (type == DELETE_RECORD) {
                            recordBigBtn.setVisibility(View.VISIBLE);
                            recordLayout.setVisibility(View.GONE);
                            hasLocalVoice = false;
                            isModify = 1;
                        } else if (type == DELETE_CONTACT) {
                            mAdapter.getDataList().remove(position);
                            mAdapter.setDataList(mAdapter.getDataList());
                            Utils.setListViewHeightBasedOnChildren(listView);
                            contactTitle.setText(String.format(getString(R.string.contact_size), mAdapter.getDataList().size()));
                        }
                    }
                });
        mAlertDialog.show();
    }

    void showChooseDialog(final int index) {
        if (mAdapter.getDataList().size() != index) {
            mEditTextDialog = new EditTextDialog(getActivity(), mAdapter.getDataList().get(index).getName(), mAdapter.getDataList().get(index).getPhone());
        } else {
            mEditTextDialog = new EditTextDialog(getActivity());
        }
        mEditTextDialog.setPositiveButton(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mEditTextDialog.getNameText()) || TextUtils.isEmpty(mEditTextDialog.getPhoneText())) {
                    ToastUtils.showShort(getActivity(), "姓名或电话不能为空！");
                    return;
                }
                List<ContactBean> listItems = mAdapter.getDataList();
                if (index == listItems.size()) {
                    if (listItems != null && listItems.size() > 0) {
                        for (ContactBean bean : listItems) {
                            if (bean.getPhone().equals(mEditTextDialog.getPhoneText().trim())) {
                                Toast.makeText(getActivity(), "该电话已存在，请输入新的号码！", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getActivity(), "该电话已存在，请输入新的号码！", Toast.LENGTH_SHORT).show();
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
        });
        mEditTextDialog.show();
    }


    //  按钮的点击事件： 打开系统联系人。
    private void openContact() {
        //检查是否有读取联系人权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {
                ToastUtils.showShort(getActivity(), "请检查是否有读取联系人权限！");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 666);
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
                ToastUtils.showShort(getActivity(), "请检查是否有读取联系人权限！");
            }
        }
        if (requestCode == 888) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initRecord();
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
     * 读取联系人信息
     *
     * @param uri
     */
    private String[] getPhoneContacts(Uri uri) {
        String[] contact = new String[2];
        //得到ContentResolver对象
        ContentResolver cr = this.getActivity().getContentResolver();
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
            voiceAnim.setBackgroundResource(R.mipmap.icn_audio3);
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

    @Override
    public void getTelphoneInfoSuccess(TelphoneInfo resp) {
        mMsgSosResp = resp;
        isModify = 0;
        hasLocalVoice = false;
        voiceTip.setVisibility(View.GONE);
        mAdapter.setDataList(resp.getContact());
        Utils.setListViewHeightBasedOnChildren(listView);
        contactTitle.setText(String.format(getString(R.string.contact_size), mAdapter.getDataList().size()));
        nameTv.setText(resp.getNickname());
        sosMsg.setText(resp.getMessage());
        callBtn.setText("报警电话（" + resp.getAlarm_type() + "）");
        if (!TextUtils.isEmpty(resp.getFile_url())) {
            recordLayout.setVisibility(View.VISIBLE);
            recordBigBtn.setVisibility(View.GONE);
        } else {
            recordLayout.setVisibility(View.GONE);
            recordBigBtn.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void saveTelphoneInfoSuccess() {
        ToastUtils.showShort(getActivity(), "数据提交成功，请五分钟后检查您的座机！");
        isModify = 0;
        hasLocalVoice = false;
        voiceTip.setVisibility(View.GONE);
        mPresenter.getTelphoneInfo(deviceId);
    }

    @Override
    public void saveTelphoneInfoFail() {
        mPresenter.getTelphoneInfo(deviceId);
    }

    @Override
    public void setPresenter(TelephoneContract.Presenter presenter) {
        mPresenter = presenter;
    }

    /**
     * 按住说话listener
     */
    class PressToSpeakListen implements View.OnTouchListener {
        float downY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ActivityCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.RECORD_AUDIO)) {
                            ToastUtils.showShort(getActivity(), "请检查是否有录音权限！");
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 888);
                            return true;
                        }
                    }

                    //录音之前暂停播放
                    releaseMediaPlayer();
                    downY = event.getY();
                    //检查sdcard
                    if (!DeviceUtils.isExitsSdcard()) {
                        String needSd = getResources().getString(R.string.send_voice_need_sdcard_support);
                        Toast.makeText(getContext(), needSd, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    // 设置输出文件
                    recorder.setOutputFile(filePath);
                    recorder.prepare();
                    recorder.start();
                    //弹出dialog
                    if (recorder.getState() != ExtAudioRecorder.State.ERROR) {
                        recordHintDialog.show();
                        recordHintDialog.moveUpToCancel();
                        return true;
                    }
                    return false;

                case MotionEvent.ACTION_MOVE: {
                    if (recorder.getState() != ExtAudioRecorder.State.RECORDING) {
                        return false;
                    }
//                    float offsetY = downY - event.getY();
//                    if (offsetY > touchSlop) {
//                        recordHintDialog.releaseToCancel();
//                    } else {
//                        recordHintDialog.moveUpToCancel();
//                    }

                    if (wantToCancel(v, x, y)) {
                        recordHintDialog.releaseToCancel();
                    } else {
                        recordHintDialog.moveUpToCancel();
                    }
                    return true;
                }
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    recordHintDialog.dismiss();
                    if (recorder.getState() != ExtAudioRecorder.State.RECORDING) {
                        return false;
                    }
                    float offsetY = downY - event.getY();
                    if (offsetY > touchSlop) {
                        //删除录音
                        recorder.discardRecording();
                    } else {
                        //录音成功
                        int time = recorder.stop();
                        if (time > 0) {
                            //成功的处理
                            isModify = 1;
                            hasLocalVoice = true;

                            try {
                                FileUtils.copyFileUsingFileStreams(tempFilePath, filePath);
                            } catch (IOException e) {
                                Logger.e(TAG, "stopRecord IOException：" + e);
                            }

                            Logger.d(TAG, "录音成功地址为：" + filePath);

                            recordLayout.setVisibility(View.VISIBLE);
                            recordBigBtn.setVisibility(View.GONE);

                            voiceTip.setVisibility(View.VISIBLE);
                            voiceLength.setText(time + "''");
                        } else {
                            String st2 = getResources().getString(R.string.the_recording_time_is_too_short);
                            Toast.makeText(getActivity(), st2, Toast.LENGTH_SHORT).show();
                        }
                    }
                    recorder.reset();
                    return true;
            }
            return false;
        }
    }

    //垂直方向滑动取消的临界距离
    private static final int DISTANCE_Y_CANCEL = 50;

    private boolean wantToCancel(View view, int x, int y) {
        if (x < 0 || x > view.getWidth()) {// 判断是否在左边，右边，上边，下边
            return true;
        }
        if (y < -DISTANCE_Y_CANCEL || y > view.getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //触摸的是EditText并且当前EditText可以滚动则将事件交给EditText处理；否则将事件交由其父类处理
        if ((view.getId() == R.id.sosMsg && canVerticalScroll(sosMsg))) {
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
