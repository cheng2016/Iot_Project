package com.cds.iot.module.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cds.iot.R;
import com.cds.iot.data.entity.Device;
import com.cds.iot.module.scenes.adddevice.AddScenesDeviceActivity;
import com.cds.iot.view.CustomDialog;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class ScenesDeviceAdapter extends BaseAdapter implements View.OnClickListener {
    public final static int MAX_SELECT_PIC_NUM = 4;

    public final static int CLICK_DEVICE_IMG = 0;

    public final static int CLICK_ADD_IMG = 1;

    private Context context;
    private List<Device> mDataList = new ArrayList<>();
    private String typeId;


    public ScenesDeviceAdapter(Context context) {
        this.context = context;
    }

    public ScenesDeviceAdapter(Context context, List<Device> list) {
        this.context = context;
        this.mDataList = list;
    }

    public void updateData(List<Device> list) {
        this.mDataList = list;
        notifyDataSetChanged();
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    @Override
    public int getCount() {
        if (mDataList.size() == MAX_SELECT_PIC_NUM) {
            return mDataList.size();
        }
        return mDataList.size() + 1;
    }

    @Override
    public Object getItem(int i) {
        return mDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup viewGroup) {
/*        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_scense_device, null, false);
            holder.deviceImg = (ImageView) convertView.findViewById(R.id.device_img);
            holder.delImg = (ImageView) convertView.findViewById(R.id.del_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.deviceImg.setOnClickListener(this);

        if (index == mDataList.size() && index < 5) {
            holder.deviceImg.setImageResource(R.mipmap.icn_adddevice);//最后一个显示加号图片
            holder.delImg.setVisibility(View.INVISIBLE);
            holder.deviceImg.setTag(CLICK_ADD_IMG);
        } else {
            Device device = mDataList.get(index);
            Picasso.with(context)
                    .load(device.getAdd_icon_url())
//                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(holder.deviceImg);
            holder.delImg.setVisibility(View.VISIBLE);
            holder.delImg.setOnClickListener(this);
            holder.delImg.setTag(device);
            holder.deviceImg.setTag(CLICK_DEVICE_IMG);
        }
        */


        convertView = LayoutInflater.from(context).inflate(R.layout.item_scense_device, null, false);
        ImageView deviceImg = (ImageView) convertView.findViewById(R.id.device_img);
        ImageView delImg = (ImageView) convertView.findViewById(R.id.del_img);

        deviceImg.setOnClickListener(this);

        if (index == mDataList.size() && index < 5) {
            deviceImg.setImageResource(R.mipmap.icn_adddevice);//最后一个显示加号图片
            delImg.setVisibility(View.INVISIBLE);
            deviceImg.setTag(CLICK_ADD_IMG);
        } else {
            Device device = mDataList.get(index);
            Picasso.with(context)
                    .load(device.getAdd_icon_url())
//                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(deviceImg);
            delImg.setVisibility(View.VISIBLE);
            delImg.setOnClickListener(this);
            delImg.setTag(device);
            deviceImg.setTag(CLICK_DEVICE_IMG);
        }

        return convertView;
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.del_img:
                new CustomDialog(context)
                        .setMessage("确认将该设备从场景移出？")
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Device device = (Device) view.getTag();
                                device.setTag("remove");
                                mDataList.remove(device);
                                notifyDataSetChanged();
                                EventBus.getDefault().post(device);
                            }
                        }).showDialog();
                break;
            case R.id.device_img:
                if (view.getTag().equals(CLICK_ADD_IMG)) {
//                ToastUtils.showShort(context,"点击添加按钮");
                    Intent intent = new Intent().setClass(context, AddScenesDeviceActivity.class);
                    intent.putExtra("typeId", typeId);
                    context.startActivity(intent);
                }
                break;
        }
    }

    class ViewHolder {
        ImageView deviceImg, delImg;
    }

    onDeviceChangeListener listener;

    public void setListener(onDeviceChangeListener listener) {
        this.listener = listener;
    }

    public interface onDeviceChangeListener {
        void onChange(String type, Device device);
    }
}
