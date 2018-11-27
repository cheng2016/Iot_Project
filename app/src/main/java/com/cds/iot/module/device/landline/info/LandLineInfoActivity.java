package com.cds.iot.module.device.landline.info;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.KeyboardUtils;
import com.cds.iot.App;
import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.data.entity.MirrorInfoResp;
import com.cds.iot.util.ToastUtils;
import com.squareup.picasso.Picasso;

import butterknife.Bind;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/11/13 17:18
 * <p>
 * 座机信息界面
 */
public class LandLineInfoActivity extends BaseActivity implements View.OnClickListener, LandLineInfoContract.View {
    @Bind(R.id.name_edit)
    EditText nickNameEdit;
    @Bind(R.id.zxing_img)
    ImageView zxingImg;
    @Bind(R.id.device_id_edit)
    AppCompatTextView deviceIdEdit;

    private String deviceName;

    private String deviceId;

    LandLineInfoContract.Presenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_landline_info;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((TextView) findViewById(R.id.title)).setText("设备信息");
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        nickNameEdit.setCursorVisible(false);// 内容清空后将编辑框1的光标隐藏，提升用户的体验度
        nickNameEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    nickNameEdit.setCursorVisible(true);// 再次点击显示光标
                    nickNameEdit.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.btn_editname), null);
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
                    nickNameEdit.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.btn_confirm), null);
                    mPresenter.saveDeviceInfo(deviceId,nickNameEdit.getText().toString());
                    return true;
                }
                return false;
            }
        });
        deviceIdEdit.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        new LandLineInfoPresenter(this);
        if(getIntent()!=null && getIntent().getExtras()!=null){
            deviceName = getIntent().getStringExtra("deviceName");
            deviceId = getIntent().getStringExtra("deviceId");
            nickNameEdit.setText(deviceName);
            mPresenter.getDeviceInfo(deviceId);
        }
        mLoadingView.showLoading();
        mLoadingView.setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getDeviceInfo(deviceId);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.device_id_edit:
                if(mInfoResp != null){
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // 将文本内容放到系统剪贴板里。
                    cm.setText(mInfoResp.getDevice_code());
                    Toast.makeText(this, "复制成功，可以发给朋友们了。", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    MirrorInfoResp mInfoResp;

    @Override
    public void getDeviceInfoSuccess(MirrorInfoResp resp) {
        mLoadingView.showContent();
        this.mInfoResp = resp;
        nickNameEdit.setCursorVisible(false);// 内容清空后将编辑框1的光标隐藏，提升用户的体验度
        nickNameEdit.setText(resp.getDevice_name());
        String deviceName = "设备ID : " + resp.getDevice_code() + "&nbsp&nbsp&nbsp<font color='#30D6DC'>复制</font>";
        deviceIdEdit.setText(Html.fromHtml(deviceName));
        Picasso.with(this).load(resp.getDevice_qrcode()).into(zxingImg);
    }

    @Override
    public void getDeviceInfoFailed() {
        mLoadingView.showError();
    }

    @Override
    public void saveDeviceInfoSuccess() {
        ToastUtils.showShort(this,"修改设备昵称成功");
        setResult(RESULT_OK,new Intent().putExtra("deviceName",nickNameEdit.getText().toString().trim()));
        mPresenter.getDeviceInfo(deviceId);
        Intent intent = new Intent();
        intent.putExtra("deviceName",nickNameEdit.getText().toString());
        setResult(RESULT_OK,intent);
    }

    @Override
    public void setPresenter(LandLineInfoContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
