package com.cds.iot.module.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.data.Constant;
import com.cds.iot.data.entity.Device;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chengzj
 * @date 2018/9/19 15:15
 * <p>
 * 管理绑定页面的设备适配器
 */
public class ManageDeviceAdapter extends BaseAdapter {
    public static final String TAG = "ManageDeviceAdapter";
    private Context context;

    private List<Device> mDataList = new ArrayList<>();

    public ManageDeviceAdapter(Context context) {
        this.context = context;
    }

    public ManageDeviceAdapter(Context context, List<Device> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    public List<Device> getDataList() {
        return mDataList;
    }

    public void setDataList(List<Device> mDataList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_manage_bind_device, null);
            holder.deviceNameTv = (TextView) convertView.findViewById(R.id.device_name);
            holder.deviceIdTv = (TextView) convertView.findViewById(R.id.device_id);
            holder.deviceImg = (ImageView) convertView.findViewById(R.id.device_img);
            holder.isAdmin = (ImageView) convertView.findViewById(R.id.is_admin);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (!mDataList.isEmpty()) {
            Device device = mDataList.get(index);
            holder.deviceNameTv.setText(device.getName());
            holder.deviceIdTv.setText(device.getCode());
            Picasso.with(context).load(device.getImg_url()).into(holder.deviceImg);
            if (Constant.DEVICE_IS_ADMIN.equals(device.getIs_admin())) {
                holder.isAdmin.setVisibility(View.VISIBLE);
            } else {
                holder.isAdmin.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    class ViewHolder {
        TextView deviceNameTv;
        TextView deviceIdTv;
        ImageView deviceImg;
        ImageView isAdmin;
    }
}