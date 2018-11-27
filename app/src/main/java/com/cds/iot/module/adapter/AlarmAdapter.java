package com.cds.iot.module.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.data.entity.AlarmInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @Author: ${auther}
 * @CreateDate: 2018/11/19 13:56
 * @Version: 3.0.0
 */
public class AlarmAdapter extends BaseAdapter {
    private Context context;
    private List<AlarmInfo> mDataList = new ArrayList<>();

    String[] mVals = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    public AlarmAdapter(Context context) {
        this.context = context;
    }

    public AlarmAdapter(Context context, List<AlarmInfo> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    public List<AlarmInfo> getDataList() {
        return mDataList;
    }

    public void setDataList(List<AlarmInfo> mDataList) {
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
    public View getView(int index, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null || view.getTag() == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_alarm_info, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        AlarmInfo bean = mDataList.get(index);

        holder.titleTv.setText(bean.getTitle());
        holder.stateTv.setText(bean.getIs_send() == 0 ? "未同步" : "已同步");
        holder.stateTv.setTextColor(bean.getIs_send() == 0 ? context.getResources().getColor(R.color.hint_color) : context.getResources().getColor(R.color.aggree_text));
        holder.stateTv.setBackgroundResource(bean.getIs_send() == 0 ? R.drawable.shape_synchronize_off : R.drawable.shape_synchronize_off);
        holder.timeTv.setText(bean.getDate());
        holder.dayTv.setText(bean.getWeek());

        List<Integer> integerSet = new ArrayList<>();
        StringBuilder sb = new StringBuilder(bean.getWeek());
        for (int i = 0; i < 7; i++) {
            if ("1".equals(sb.substring(i, i + 1))) {
                integerSet.add(i);
            }
        }
        //排序
        Integer[] integers = new Integer[integerSet.size()];
        integers = bubbleSort(integerSet.toArray(integers));
        StringBuilder days = new StringBuilder();
        for (Integer i: integers){
            days.append(mVals[i]).append("\r");
        }

        holder.dayTv.setText(days.toString());

        if (bean.getState() == 0) {
            holder.switchBtn.setChecked(false);
        } else {
            holder.switchBtn.setChecked(true);
        }

        if (TextUtils.isEmpty(bean.getRecord_file())) {
            holder.voiceImg.setVisibility(View.INVISIBLE);
            holder.lengthTv.setVisibility(View.INVISIBLE);
        } else {
            holder.voiceImg.setVisibility(View.VISIBLE);
            holder.lengthTv.setVisibility(View.VISIBLE);
            holder.lengthTv.setText(bean.getRecord_duration());
        }

        holder.switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCheckBoxChange(index, holder.switchBtn.isChecked());
                }
            }
        });

        holder.voiceImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onVoiceClick(holder.voiceImg, index);
                }
            }
        });

        holder.deleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDeleteClick(index);
                }
            }
        });
        return view;
    }

    /**
     * 冒泡排序
     * 比较相邻的元素。如果第一个比第二个大，就交换他们两个。
     * 对每一对相邻元素作同样的工作，从开始第一对到结尾的最后一对。在这一点，最后的元素应该会是最大的数。
     * 针对所有的元素重复以上的步骤，除了最后一个。
     * 持续每次对越来越少的元素重复上面的步骤，直到没有任何一对数字需要比较。
     * @param numbers 需要排序的整型数组
     */
    public Integer[] bubbleSort(Integer[] numbers)
    {
        int temp = 0;
        int size = numbers.length;
        for(int i = 0 ; i < size-1; i ++)
        {
            for(int j = 0 ;j < size-1-i ; j++)
            {
                if(numbers[j] > numbers[j+1])  //交换两数位置
                {
                    temp = numbers[j];
                    numbers[j] = numbers[j+1];
                    numbers[j+1] = temp;
                }
            }
        }
        return numbers;
    }

    onAlarmChangeListener listener;

    public void setListener(onAlarmChangeListener listener) {
        this.listener = listener;
    }

    public interface onAlarmChangeListener {
        void onDeleteClick(int index);

        void onCheckBoxChange(int index, boolean isCheck);

        void onVoiceClick(ImageView voiceAnim, int index);
    }

    static class ViewHolder {
        @Bind(R.id.title_tv)
        TextView titleTv;
        @Bind(R.id.time_tv)
        TextView timeTv;
        @Bind(R.id.state_tv)
        TextView stateTv;
        @Bind(R.id.switch_btn)
        CheckBox switchBtn;
        @Bind(R.id.day_tv)
        TextView dayTv;
        @Bind(R.id.voice_img)
        ImageView voiceImg;
        @Bind(R.id.length_tv)
        TextView lengthTv;
        @Bind(R.id.delete_tv)
        TextView deleteTv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
