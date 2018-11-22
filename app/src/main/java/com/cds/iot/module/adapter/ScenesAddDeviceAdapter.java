package com.cds.iot.module.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.data.entity.Device;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ScenesAddDeviceAdapter extends BaseAdapter {
    private Context context;
    private List<Device> mDataList = new ArrayList<>();

    public ScenesAddDeviceAdapter(Context context) {
        this.context = context;
    }

    public ScenesAddDeviceAdapter(Context context, List<Device> list) {
        this.context = context;
        this.mDataList = list;
    }

    public void updateData(List<Device> list) {
        this.mDataList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDataList.size();
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
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_add_scense_device, null, false);
            holder.deviceImg = (ImageView) convertView.findViewById(R.id.device_img);
            holder.deviceNameTv = (TextView) convertView.findViewById(R.id.device_name_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mDataList.size() > 0) {
            Device device = mDataList.get(index);
            Picasso.with(context).load(device.getImg_url()).into(holder.deviceImg);
            holder.deviceNameTv.setText(device.getName());
        }

        return convertView;
    }

    class ViewHolder {
        ImageView deviceImg;
        TextView deviceNameTv;
    }
}
