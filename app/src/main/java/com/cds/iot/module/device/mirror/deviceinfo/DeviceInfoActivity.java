package com.cds.iot.module.device.mirror.deviceinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.cds.iot.App;
import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.data.entity.MirrorInfoResp;
import com.cds.iot.util.ToastUtils;

import butterknife.Bind;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/10 17:53
 *
 * 设备详情
 */
public class DeviceInfoActivity extends BaseActivity implements View.OnClickListener, DeviceInfoContract.View {
    @Bind(R.id.device_img)
    AppCompatImageView deviceImg;
    @Bind(R.id.nick_name)
    EditText nickNameEdit;
    @Bind(R.id.device_model)
    TextView deviceModelTv;
    @Bind(R.id.serial_number)
    TextView serialNumberTv;

    private String deviceId = "";

    DeviceInfoContract.Presenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_info;
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
                    mPresenter.saveMirrorInfo(deviceId,nickNameEdit.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void initData() {
        new DeviceInfoPresenter(this);
        if(getIntent() !=null && getIntent().getExtras()!=null){
            deviceId = getIntent().getStringExtra("deviceId");
            mPresenter.getMirrorInfo(deviceId);
        }
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
            default:
                break;
        }
    }

    @Override
    public void getMirrorInfoSuccess(MirrorInfoResp resp) {
        nickNameEdit.setCursorVisible(false);// 内容清空后将编辑框1的光标隐藏，提升用户的体验度
        nickNameEdit.setText(resp.getDevice_name());
        deviceModelTv.setText(resp.getDevice_type());
        serialNumberTv.setText(resp.getDevice_code());
    }

    @Override
    public void saveMirrorInfoSuccess() {
        ToastUtils.showShort(this,"修改设备昵称成功");
        setResult(RESULT_OK,new Intent().putExtra("deviceName",nickNameEdit.getText().toString().trim()));
        mPresenter.getMirrorInfo(deviceId);
    }

    @Override
    public void setPresenter(DeviceInfoContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
