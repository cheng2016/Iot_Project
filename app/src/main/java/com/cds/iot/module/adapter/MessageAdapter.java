package com.cds.iot.module.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cds.iot.R;
import com.cds.iot.data.Constant;
import com.cds.iot.data.entity.SMessage;
import com.cds.iot.util.DateUtils;
import com.cds.iot.util.picasso.PicassoRoundTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Chengzj
 * @date 2018/9/29 15:58
 */
public class MessageAdapter extends BaseAdapter {
    Context context;
    List<SMessage> mDataList = new ArrayList<>();

    public MessageAdapter(Context context) {
        this.context = context;
    }

    public MessageAdapter(Context context, List<SMessage> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    public List<SMessage> getDataList() {
        return mDataList;
    }

    public void setDataList(List<SMessage> mDataList) {
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
    public View getView(final int index, View view, ViewGroup viewGroup) {
        SMessage bean = mDataList.get(index);
        int type = Integer.parseInt(bean.getMsgType());
        ApplyForViewHolder holder;
        MirrorViewHolder mirrorHolder;
        switch (type) {
            case Constant.MESSAGE_TYPE_USER_ADD_APPLY:
            case Constant.MESSAGE_TYPE_USER_ADD_APPLY_REFUSE:
            case Constant.MESSAGE_TYPE_USER_ADD_APPLY_AGREE:
            case Constant.MESSAGE_TYPE_ADMIN_REMOVE_USER_DEVICE:
            case Constant.MESSAGE_TYPE_USER_REMOVE_DEVICE:
            case Constant.MESSAGE_TYPE_ADMIN_REMOVE_DEVICE:
            case Constant.MESSAGE_TYPE_ADMIN_REPLACE:
            case Constant.MESSAGE_TYPE_ADMIN_REPLACE_NOTIFY_USER:
                if (view == null || view.getTag() == null || !(view.getTag() instanceof ApplyForViewHolder)) {
                    view = LayoutInflater.from(context).inflate(R.layout.item_message, null);
                    holder = new ApplyForViewHolder(view);
                    view.setTag(holder);
                } else {
                    holder = (ApplyForViewHolder) view.getTag();
                }
                String content = "<font color='#849DFF'>" + bean.getTitle() + "</font>   " + bean.getContent();
                holder.titleTv.setText(bean.getDeviceName());
                holder.contentTv.setText(Html.fromHtml(content));
                Picasso.with(context).load(bean.getDeviceImg()).into(holder.deviceImg);
                holder.timeTv.setText(DateUtils.timeStampDate(bean.getTailtime()));
                if (type == Constant.MESSAGE_TYPE_USER_REMOVE_DEVICE
                        || type == Constant.MESSAGE_TYPE_ADMIN_REMOVE_DEVICE
                        || type == Constant.MESSAGE_TYPE_ADMIN_REPLACE
                        || type == Constant.MESSAGE_TYPE_ADMIN_REPLACE_NOTIFY_USER) {
                    holder.moreTv.setVisibility(View.GONE);
                }else {
                    holder.moreTv.setVisibility(View.VISIBLE);
                }
                break;
            case Constant.MESSAGE_TYPE_DEVICE_IMAGE:
            case Constant.MESSAGE_TYPE_DEVICE_VIDEO:
                if (view == null || view.getTag() == null || !(view.getTag() instanceof MirrorViewHolder)) {
                    view = LayoutInflater.from(context).inflate(R.layout.item_message_mirror, null);
                    mirrorHolder = new MirrorViewHolder(view);
                    view.setTag(mirrorHolder);
                } else {
                    mirrorHolder = (MirrorViewHolder) view.getTag();
                }
                mirrorHolder.titleTv.setText(bean.getDeviceName());
                mirrorHolder.contentTv.setText(bean.getTitle());
                Picasso.with(context).load(bean.getDeviceImg()).into(mirrorHolder.deviceImg);
                Picasso.with(context).load(bean.getPhotoUrl()).transform(new PicassoRoundTransform()).into(mirrorHolder.photoImg);
                mirrorHolder.timeTv.setText(DateUtils.timeStampDate(bean.getTailtime()));
                mirrorHolder.playImg.setVisibility(type == Constant.MESSAGE_TYPE_DEVICE_IMAGE ? View.GONE : View.VISIBLE);

                mirrorHolder.photoImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (lisenter != null) {
                            lisenter.onImageClick(index);
                        }
                    }
                });
                mirrorHolder.playImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (lisenter != null) {
                            lisenter.onPlayClick(index);
                        }
                    }
                });
                break;
            case Constant.MESSAGE_TYPE_IN_REGION_ALARM:
            case Constant.MESSAGE_TYPE_OUT_REGION_ALARM:

                break;
            case Constant.MESSAGE_TYPE_CAR_BUMP:

                break;
            case Constant.MESSAGE_TYPE_LOW_ELECTRIC_ALARM:

                break;
            default:
                break;
        }
        return view;
    }

    static class ApplyForViewHolder {
        @Bind(R.id.device_img)
        AppCompatImageView deviceImg;
        @Bind(R.id.time)
        AppCompatTextView timeTv;
        @Bind(R.id.title)
        AppCompatTextView titleTv;
        @Bind(R.id.content)
        AppCompatTextView contentTv;
        @Bind(R.id.more)
        AppCompatTextView moreTv;

        ApplyForViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class MirrorViewHolder {
        @Bind(R.id.device_img)
        AppCompatImageView deviceImg;
        @Bind(R.id.time)
        AppCompatTextView timeTv;
        @Bind(R.id.title)
        AppCompatTextView titleTv;
        @Bind(R.id.content)
        AppCompatTextView contentTv;
        @Bind(R.id.photo)
        AppCompatImageView photoImg;
        @Bind(R.id.play)
        AppCompatImageView playImg;

        MirrorViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    OnMessageLisenter lisenter;

    public void setLisenter(OnMessageLisenter lisenter) {
        this.lisenter = lisenter;
    }

    public interface OnMessageLisenter {
        void onImageClick(int index);

        void onPlayClick(int index);
    }
}
