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
import com.cds.iot.data.entity.DeviceUser;
import com.cds.iot.util.picasso.PicassoCircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chengzj
 * @date 2018/9/19 17:20
 * <p>
 * 绑定管理详情页的用户适配器
 */
public class ManageUserAdapter extends BaseAdapter {
    public static final String TAG = "ManageUserAdapter";
    private Context context;
    private List<DeviceUser> mDataList = new ArrayList<>();
    private boolean isEdit = false;

    public ManageUserAdapter(Context context) {
        this.context = context;
    }

    public ManageUserAdapter(Context context, List<DeviceUser> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    public List<DeviceUser> getDataList() {
        return mDataList;
    }

    public void setDataList(List<DeviceUser> mDataList) {
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

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    @Override
    public View getView(final int index, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_manage_bind_user, null);
            holder.userImg = (ImageView) convertView.findViewById(R.id.head);
            holder.userName = (TextView) convertView.findViewById(R.id.user_name);
            holder.delete = (ImageView) convertView.findViewById(R.id.delete);
            holder.isAdminImg = (ImageView) convertView.findViewById(R.id.is_admin);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (index == 0) {
            holder.isAdminImg.setVisibility(View.VISIBLE);
        } else {
            holder.isAdminImg.setVisibility(View.GONE);
        }

        if (isEdit) {
            if (index != 0) {//管理员不显示删除按钮
                holder.delete.setVisibility(View.VISIBLE);
            }
        } else {
            holder.delete.setVisibility(View.GONE);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onDeleteClick(index);
                }
            }
        });

        if (mDataList.get(index) != null) {
            DeviceUser bean = mDataList.get(index);
            holder.userName.setText(bean.getName());
            if (TextUtils.isEmpty(bean.getImg_url())) {
                holder.userImg.setImageResource(R.mipmap.btn_male_big);
            }else {
                Picasso.with(context).load(bean.getImg_url()).placeholder(R.mipmap.btn_male_big).transform(new PicassoCircleTransform()).into(holder.userImg);
            }
        }
        return convertView;
    }

    class ViewHolder {
        TextView userName;
        ImageView userImg;
        ImageView delete;
        ImageView isAdminImg;
    }

    OnDeleteListener listener;

    public void setListener(OnDeleteListener listener) {
        this.listener = listener;
    }

    public interface OnDeleteListener {
        void onDeleteClick(int index);
    }
}
