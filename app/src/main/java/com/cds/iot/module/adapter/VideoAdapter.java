package com.cds.iot.module.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.data.entity.VideoEntity;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator Chengzj
 *
 * @date 2018/10/31 17:53
 */
public class VideoAdapter extends BaseAdapter {
    Context context;
    List<VideoEntity> mDataList = new ArrayList<>();

    public VideoAdapter(Context context) {
        this.context = context;
    }

    public VideoAdapter(Context context, List<VideoEntity> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    public List<VideoEntity> getDataList() {
        return mDataList;
    }

    public void setDataList(List<VideoEntity> mDataList) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_mirror_video, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        VideoEntity bean = mDataList.get(position);

        Picasso.with(context).load(bean.getImagePath()).into(holder.deviceImg);

        holder.timeTv.setText(bean.getLength());

        if (TextUtils.isEmpty(bean.getLength())) {
            holder.timeTv.setVisibility(View.GONE);
        } else {
            holder.timeTv.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(bean.getVideoPath())) {
            holder.playImg.setVisibility(View.GONE);
        } else {
            holder.playImg.setVisibility(View.VISIBLE);
        }

        holder.playImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(bean.getVideoPath()) && listener != null) {
                    listener.onPlayClick(bean.getName(), bean.getVideoPath());
                }
            }
        });

        holder.deviceImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(bean.getVideoPath()) && listener != null) {
                    listener.onImageClick(bean.getImagePath());
                }
            }
        });

        holder.downloadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && bean.getProgress() == null) {
                    if (!TextUtils.isEmpty(bean.getVideoPath())) {
                        listener.onDownloadClick(holder.downloadImg, position, bean.getVideoPath());
                    } else {
                        listener.onDownloadClick(holder.downloadImg, position, bean.getImagePath());
                    }
                }
            }
        });

        holder.downloadImg.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.mipmap.ic_btn_download), null, null);
        if (bean.getProgress() == null) {
            holder.downloadImg.setText("");
        } else if (bean.getProgress() == 100) {
            holder.downloadImg.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.mipmap.ic_btn_downloadfinished), null, null);
            holder.downloadImg.setText("完成");
        } else if (bean.getProgress() == -1) {
            holder.downloadImg.setText("失败");
        } else {
            holder.downloadImg.setText(String.format("%d", bean.getProgress()) + "%");
        }

        return view;
    }

    OnDownloadClickListener listener;

    public void seOnDownloadtListener(OnDownloadClickListener listener) {
        this.listener = listener;
    }

    public interface OnDownloadClickListener {
        void onDownloadClick(TextView textView, int index, String path);

        void onPlayClick(String name, String videoPath);

        void onImageClick(String imagePath);
    }

    static class ViewHolder {
        @Bind(R.id.device_img)
        ImageView deviceImg;
        @Bind(R.id.play_img)
        ImageView playImg;

        @Bind(R.id.download_img)
        AppCompatTextView downloadImg;

        @Bind(R.id.time_tv)
        TextView timeTv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
