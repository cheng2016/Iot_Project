package com.cds.iot.module.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.data.entity.MessageType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chengzj
 * @date 2018/9/26 17:24
 */
public class OptionAdapter extends BaseAdapter {
    Context context;
    List<MessageType> mDataList = new ArrayList<>();

    Integer selectItem;

    public OptionAdapter(Context context) {
        this.context = context;
    }

    public OptionAdapter(Context context, List<MessageType> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    public List<MessageType> getDataList() {
        return mDataList;
    }

    public void setDataList(List<MessageType> mDataList) {
        this.mDataList = mDataList;
        notifyDataSetChanged();
    }

    public void setSelect(int select) {
        this.selectItem = select;
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
    public View getView(int index, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null || view.getTag() == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_popwindow_option, null);
            holder.option = (TextView) view.findViewById(R.id.option);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (!mDataList.isEmpty()) {
            holder.option.setText(mDataList.get(index).getMessage_type_name());
        }

        if (selectItem == index) {
            holder.option.setTextColor(context.getResources().getColor(R.color.theme_color));
        } else {
            holder.option.setTextColor(context.getResources().getColor(R.color.popwindow_option_text));
        }
        return view;
    }

    class ViewHolder {
        TextView option;
    }
}
