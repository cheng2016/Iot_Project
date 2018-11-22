package com.cds.iot.module.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.data.entity.Device;
import com.cds.iot.data.entity.DeviceType;

import java.util.ArrayList;
import java.util.List;

public class ScenesDeviceTypeAdapter extends BaseAdapter {
    private Context context;
    private List<DeviceType> mDataList = new ArrayList<>();

    public ScenesDeviceTypeAdapter(Context context) {
        this.context = context;
    }

    public ScenesDeviceTypeAdapter(Context context, List<DeviceType> list) {
        this.context = context;
        this.mDataList = list;
    }

    public List<DeviceType> getDataList() {
        return mDataList;
    }

    public void setDataList(List<DeviceType> mDataList) {
        this.mDataList = mDataList;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_scense_device_type, null, false);
            holder.deviceTypeName = (TextView) convertView.findViewById(R.id.device_type_name);
            holder.horizontalListView = (GridView) convertView.findViewById(R.id.device_horizontal_listview);
            holder.adapter = new ScenesDeviceAdapter(context);
            holder.horizontalListView.setAdapter(holder.adapter);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (!mDataList.isEmpty()) {
            DeviceType deviceType = mDataList.get(index);
            holder.deviceTypeName.setText(deviceType.getName() + "：");
            if (deviceType.getDevices() != null && !deviceType.getDevices().isEmpty()) {
                for (Device device : deviceType.getDevices()) {
                    device.setAdd_icon_url(deviceType.getAdd_icon_url());
                    device.setType_id(deviceType.getId());
                }
                holder.adapter.updateData(deviceType.getDevices());
            } else {
                holder.adapter.updateData(new ArrayList<>());
            }
            holder.adapter.setTypeId(deviceType.getId());
        }
        return convertView;
    }

    class ViewHolder {
        TextView deviceTypeName;
        GridView horizontalListView;
        ScenesDeviceAdapter adapter;
    }
}
