package com.cds.iot.module.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.help.Tip;
import com.cds.iot.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/16 16:55
 */
public class AutoCompleteTextAdapter extends BaseAdapter {
    Context context;
    List<Tip> mDataList = new ArrayList<>();


    public AutoCompleteTextAdapter(Context context) {
        this.context = context;
    }

    public AutoCompleteTextAdapter(Context context, List<Tip> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    public List<Tip> getDataList() {
        return mDataList;
    }

    public void setDataList(List<Tip> mDataList) {
        this.mDataList = mDataList;
        this.notifyDataSetChanged();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_auto_complete_text, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.placeName.setText(mDataList.get(position).getName());
        holder.placeDetail.setText(mDataList.get(position).getAddress());

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.place_name)
        TextView placeName;
        @Bind(R.id.place_detail)
        TextView placeDetail;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
