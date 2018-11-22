package com.cds.iot.module.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.data.entity.DeviceListResp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chengzj
 * @date 2018/9/19 15:14
 *
 * 管理绑定页面的菜单适配器
 *
 */
public class ManageMenuAdapter extends BaseAdapter {
    public static final String TAG = "ManageMenuAdapter";
    private Context context;

    private List<DeviceListResp> mDataList = new ArrayList<>();

    private int selectItem = -1;

    public ManageMenuAdapter(Context context) {
        this.context = context;
    }

    public ManageMenuAdapter(Context context, List<DeviceListResp> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    public List<DeviceListResp> getDataList() {
        return mDataList;
    }

    public void setDataList(List<DeviceListResp> mDataList) {
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

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    public int getSelectItem() {
        return selectItem;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_manage_bind_menu, null);
            holder.view = convertView.findViewById(R.id.portalLinearLayout);
            holder.arrow = (ImageView) convertView.findViewById(R.id.arrow);
            holder.name = (TextView) convertView.findViewById(R.id.menu_name_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (selectItem == index) {
            holder.view.setBackgroundColor(Color.WHITE);
            holder.arrow.setVisibility(View.VISIBLE);
        } else {
            holder.view.setBackgroundColor(context.getResources().getColor(R.color.app_content_bg));
            holder.arrow.setVisibility(View.INVISIBLE);
        }

        holder.name.setText(mDataList.get(index).getName());

        return convertView;
    }

    class ViewHolder {
        ImageView arrow;
        TextView name;
        View view;
    }
}
