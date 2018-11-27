package com.cds.iot.module.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.data.entity.UserBindHistory;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/11/15 17:13
 */
public class UserHistoryAdapter extends BaseAdapter {
    Context context;
    List<UserBindHistory> mDataList = new ArrayList<>();

    public UserHistoryAdapter(Context context) {
        this.context = context;
    }

    public UserHistoryAdapter(Context context, List<UserBindHistory> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    public List<UserBindHistory> getDataList() {
        return mDataList;
    }

    public void setDataList(List<UserBindHistory> mDataList) {
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
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null || view.getTag() == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_user_history, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        UserBindHistory bean = mDataList.get(position);

//        String[] states = context.getResources().getStringArray(R.array.user_state);
//        String state = states[Integer.parseInt(bean.getState())];
        holder.stateTv.setText(bean.getState_value());

        if ("审核中".equals(bean.getState_value())) {
            holder.stateTv.setTextColor(context.getResources().getColor(R.color.under_review_text));
        } else if ("已同意".equals(bean.getState_value())) {
            holder.stateTv.setTextColor(context.getResources().getColor(R.color.aggree_text));
        } else if ("已拒绝".equals(bean.getState_value()) || "已解除".equals(bean.getState_value())){
            holder.stateTv.setTextColor(context.getResources().getColor(R.color.confirm_text_color));
        }else {
            holder.stateTv.setTextColor(context.getResources().getColor(R.color.disable_text));
        }
//        switch (Integer.parseInt(bean.getState())){
//            case Constant.DEVICE_STATE_WAIT_AGREE://待通过
//                holder.stateTv.setTextColor(context.getResources().getColor(R.color.under_review_text));
//                break;
//            case Constant.DEVICE_STATE_REFUSE://被拒绝
//                holder.stateTv.setTextColor(context.getResources().getColor(R.color.confirm_text_color));
//                break;
//            case Constant.DEVICE_STATE_INSERT_SUCCESS://已同意
//                holder.stateTv.setTextColor(context.getResources().getColor(R.color.aggree_text));
//                break;
//            default:
//                holder.stateTv.setTextColor(context.getResources().getColor(R.color.disable_text));
//                break;
//        }
        holder.nickNameTv.setText(bean.getName());
        holder.timeTv.setText(bean.getTime());
        return view;
    }

    static class ViewHolder {
        @Bind(R.id.nick_name_tv)
        TextView nickNameTv;
        @Bind(R.id.state_tv)
        TextView stateTv;
        @Bind(R.id.time_tv)
        TextView timeTv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
