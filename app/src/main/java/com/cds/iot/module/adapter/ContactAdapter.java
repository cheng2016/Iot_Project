package com.cds.iot.module.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.data.entity.ContactBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ContactAdapter extends BaseAdapter {
    List<ContactBean> mDataList = new ArrayList<>();
    Context context;

    public ContactAdapter(Context context) {
        this.context = context;
    }

    public ContactAdapter(Context context, List<ContactBean> list) {
        this.mDataList = list;
        this.context = context;
    }

    public void setDataList(List<ContactBean> listItems) {
        this.mDataList = listItems;
        this.notifyDataSetChanged();
    }

    public List<ContactBean> getDataList() {
        return mDataList;
    }

    @Override
    public int getCount() {
        if (mDataList.size() == 5) {
            return mDataList.size();
        }
        return mDataList.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_contact, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == mDataList.size() && position < 5) { //在最后位置增加一个加号图片
            holder.nameTv.setText("");
            holder.phoneTv.setText("");
            holder.addTv.setVisibility(View.VISIBLE);
            holder.contactContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                        listener.onItemClick(view, position);
                }
            });
            holder.contactContainer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return false;
                }
            });
            return convertView;
        }
        holder.addTv.setVisibility(View.GONE);
        holder.nameTv.setText(mDataList.get(position).getName());
        holder.phoneTv.setText(mDataList.get(position).getPhone());

        holder.contactContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onItemClick(view, position);
            }
        });
        holder.contactContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (listener != null) {
                    listener.onLongClick(view, position);
                }
                return false;
            }
        });
        return convertView;
    }

    public interface AddOnClickListener {
        void onItemClick(View view, int position);

        boolean onLongClick(View view, int position);
    }

    AddOnClickListener listener;

    public void setAddOnClickListener(AddOnClickListener listener) {
        this.listener = listener;
    }

    class ViewHolder {
        @Bind(R.id.name)
        TextView nameTv;
        @Bind(R.id.add)
        TextView addTv;
        @Bind(R.id.phone)
        TextView phoneTv;
        @Bind(R.id.contact_container)
        RelativeLayout contactContainer;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
