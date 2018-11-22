package com.cds.iot.module.adapter;

import android.content.Context;
import android.text.TextUtils;
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

public class DeviceAdapter extends BaseAdapter {
    private Context context;
    private List<Device> mDataList = new ArrayList<>();

    public DeviceAdapter(Context context) {
        this.context = context;
    }

    public DeviceAdapter(Context context, List<Device> list) {
        this.context = context;
        this.mDataList = list;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_device, null);
            holder.deviceNameTv = (TextView) convertView.findViewById(R.id.device_name);
            holder.deviceStatusTv = (TextView) convertView.findViewById(R.id.device_status);
            holder.deviceImg = (ImageView) convertView.findViewById(R.id.device_img);
            holder.deviceStateImg = (ImageView) convertView.findViewById(R.id.device_state);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

/*        if(mDataList.isEmpty()){
            for (int i = 0; i < 4; i++) {
                Device device = new Device();
                device.setName("别克车分析仪");
                mDataList.add(device);
            }
        }*/

        if (!mDataList.isEmpty()) {
            Device bean = mDataList.get(index);
            holder.deviceNameTv.setText(bean.getName());
            Picasso.with(context).load(bean.getMain_img_url()).into(holder.deviceImg);
            if (!TextUtils.isEmpty(bean.getState())) {
                switch (Integer.parseInt(bean.getState())) {
                    case Constant.DEVICE_STATE_WAIT_AGREE:
                        holder.deviceStateImg.setVisibility(View.VISIBLE);
                        holder.deviceStateImg.setImageResource(R.mipmap.icn_devicewait_yellow);
                        holder.deviceStatusTv.setText("待通过");
                        holder.deviceStatusTv.setTextColor(context.getResources().getColor(R.color.hint_color));
                        holder.deviceStatusTv.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.icn_devicerelieve_grey), null, null, null);
                        break;
                    case Constant.DEVICE_STATE_REFUSE:
                        holder.deviceStateImg.setVisibility(View.VISIBLE);
                        holder.deviceStateImg.setImageResource(R.mipmap.icn_devicerelieve_red);
                        holder.deviceStatusTv.setText("被拒绝");
                        holder.deviceStatusTv.setTextColor(context.getResources().getColor(R.color.hint_color));
                        holder.deviceStatusTv.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.icn_devicerelieve_grey), null, null, null);
                        break;
                    case Constant.DEVICE_STATE_REVOKE:
                        holder.deviceStateImg.setVisibility(View.VISIBLE);
                        holder.deviceStateImg.setImageResource(R.mipmap.icn_devicerelieve_red);
                        holder.deviceStatusTv.setText("已解除");
                        holder.deviceStatusTv.setTextColor(context.getResources().getColor(R.color.hint_color));
                        holder.deviceStatusTv.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.icn_devicerelieve_grey), null, null, null);
                        break;
                    case Constant.DEVICE_STATE_INSERT_SUCCESS:
                        holder.deviceStateImg.setVisibility(View.GONE);
                        if (Constant.DEVICE_ONLINE.equals(bean.getOnline())) {
                            holder.deviceStatusTv.setText("在线");
                            holder.deviceStatusTv.setTextColor(context.getResources().getColor(R.color.theme_color));
                            holder.deviceStatusTv.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.icn_online), null, null, null);
                        } else {
                            holder.deviceStatusTv.setText("离线");
                            holder.deviceStatusTv.setTextColor(context.getResources().getColor(R.color.hint_color));
                            holder.deviceStatusTv.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.icn_offline), null, null, null);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        return convertView;
    }

    class ViewHolder {
        TextView deviceNameTv;
        TextView deviceStatusTv;
        ImageView deviceImg;
        ImageView deviceStateImg;
    }
}
