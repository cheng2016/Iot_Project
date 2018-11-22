package com.cds.iot.module.scenes;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.cds.iot.App;
import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.data.entity.BindScenes;
import com.cds.iot.data.entity.Device;
import com.cds.iot.data.entity.DeviceType;
import com.cds.iot.data.entity.SceneInfo;
import com.cds.iot.data.entity.SceneReq;
import com.cds.iot.module.adapter.ScenesDeviceTypeAdapter;
import com.cds.iot.util.PreferenceConstants;
import com.cds.iot.util.PreferenceUtils;
import com.cds.iot.util.ToastUtils;
import com.cds.iot.view.CustomDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 场景编辑页面
 */
public class ScenesEditActivity extends BaseActivity implements View.OnClickListener, ScenesEditContract.View {
    @Bind(R.id.device_type_listview)
    ListView deviceTypeListview;
    @Bind(R.id.scene_img)
    ImageView sceneImg;

    EditText scenesNameEdit;

    ScenesEditPresenter mPresenter;

    ScenesDeviceTypeAdapter mAdapter;
    SceneInfo mSceneInfo;

    BindScenes mBindScenes;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scenes_edit;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.del_layout).setOnClickListener(this);
        scenesNameEdit = (EditText) findViewById(R.id.scenes_name_edit);
        scenesNameEdit.setCursorVisible(false);// 内容清空后将编辑框1的光标隐藏，提升用户的体验度
        scenesNameEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    scenesNameEdit.setCursorVisible(true);// 再次点击显示光标
                    scenesNameEdit.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.btn_modification_mine), null);
                }
                return false;
            }
        });
        scenesNameEdit.setImeOptions(EditorInfo.IME_ACTION_SEND);
        scenesNameEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
//                    ToastUtils.showShort(App.getInstance(), "键盘回车处理");
                    scenesNameEdit.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.btn_savename_mine), null);
                    KeyboardUtils.hideSoftInput(v);
                    updateScenes();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void initData() {
        new ScenesEditPresenter(this);
        ((TextView) findViewById(R.id.title)).setText("场景");
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            mSceneInfo = (SceneInfo) bundle.getSerializable("SceneInfo");
            int index = bundle.getInt("index");
            mBindScenes = mSceneInfo.getBind_scenes().get(index);
            scenesNameEdit.setText(mBindScenes.getName());
            scenesNameEdit.setSelection(mBindScenes.getName().length());
            Picasso.with(this).load(mBindScenes.getImg_url())
                    .placeholder(R.mipmap.stage_loading)
                    .error(R.mipmap.stage_loading)
                    .into(sceneImg);

            mAdapter = new ScenesDeviceTypeAdapter(this, mBindScenes.getDevice_types());
//            mAdapter.updateData(mBindScenes.getDevice_types());
            deviceTypeListview.setAdapter(mAdapter);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.del_layout:
                new CustomDialog(this)
                        .setMessage("确认删除该场景吗？")
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mBindScenes != null && !TextUtils.isEmpty(mBindScenes.getUser_scene_id()))
                                    mPresenter.deleteScenes(mBindScenes.getUser_scene_id());
                            }
                        }).showDialog();

                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    @Override
    public void onEventMainThread(Object event) {
        super.onEventMainThread(event);
        if (event instanceof Device) {
            Device device = (Device) event;
            for (DeviceType deviceType : mBindScenes.getDevice_types()) {
                if (device.getType_id().equals(deviceType.getId())) {
                    if (device.getTag().equals("add")) {
                        device.setTag(null);
                        if (deviceType.getDevices() != null) {
                            deviceType.getDevices().add(device);
                        } else {
                            List<Device> devices = new ArrayList<>();
                            devices.add(device);
                            deviceType.setDevices(devices);
                        }
                    }
                }
            }
            mAdapter.setDataList(mBindScenes.getDevice_types());
            updateScenes();
        }
    }

    private void updateScenes() {
        SceneReq req = new SceneReq();
        req.setScene_id(mBindScenes.getId());
        req.setUser_scene_id(mBindScenes.getUser_scene_id());
        req.setScene_name(scenesNameEdit.getText().toString());
        req.setUser_id(Integer.parseInt(PreferenceUtils.getPrefString(App.getInstance(), PreferenceConstants.USER_ID,"")));
        List<Device> devices = new ArrayList<>();
        for (DeviceType type : mBindScenes.getDevice_types()) {
            if (type.getDevices() != null) {
                for (Device d : type.getDevices()) {
                    devices.add(d);
                }
            }
        }
        req.setDevices(devices);
        mPresenter.updateScenes(req);
    }

    @Override
    public void setPresenter(ScenesEditContract.Presenter presenter) {
        mPresenter = (ScenesEditPresenter) presenter;
    }

    @Override
    public void deleteScenesSuccess() {
        ToastUtils.showShort(this, "删除该场景成功！");
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void updateScenesSuccess() {
        scenesNameEdit.setCursorVisible(false);// 内容清空后将编辑框1的光标隐藏，提升用户的体验度
        ToastUtils.showShort(this, "修改该场景成功！");
        setResult(RESULT_OK);
        mPresenter.getScenesImg(mBindScenes.getId());
    }

    @Override
    public void getScenesImgSuccess(String url) {
        Picasso.with(this).load(url)
                .placeholder(R.mipmap.stage_loading)
                .error(R.mipmap.stage_loading)
                .into(sceneImg);
    }
}
