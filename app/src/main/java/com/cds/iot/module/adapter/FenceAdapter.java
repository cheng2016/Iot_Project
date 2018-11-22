package com.cds.iot.module.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cds.iot.R;
import com.cds.iot.data.Constant;
import com.cds.iot.data.entity.FenceInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Chengzj
 * @date 2018/9/27 17:16
 * <p>
 * 电子围栏适配器
 */
public class FenceAdapter extends BaseAdapter {
    private Context context;
    private List<FenceInfo> mDataList = new ArrayList<>();

    public FenceAdapter(Context context) {
        this.context = context;
    }

    public FenceAdapter(Context context, List<FenceInfo> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    public List<FenceInfo> getDataList() {
        return mDataList;
    }

    public void setDataList(List<FenceInfo> mDataList) {
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
        ViewHolder holder;
        if (view == null || view.getTag() == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_electric_fence, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (!mDataList.isEmpty()) {
            FenceInfo bean = mDataList.get(index);
            if (bean.getType() == Constant.FENCE_TYPE_HOME) {
                holder.leftLayout.setBackground(context.getResources().getDrawable(R.drawable.shape_right_purple));
                holder.leftIcon.setImageResource(R.mipmap.icn_home);
                holder.delete.setVisibility(View.GONE);
            } else if (bean.getType() == Constant.FENCE_TYPE_COMPANY) {
                holder.leftLayout.setBackground(context.getResources().getDrawable(R.drawable.shape_right_pink));
                holder.leftIcon.setImageResource(R.mipmap.icn_company);
                holder.delete.setVisibility(View.GONE);
            } else {
                holder.leftLayout.setBackground(context.getResources().getDrawable(R.drawable.shape_right_blue));
                holder.leftIcon.setImageResource(R.mipmap.icn_temporary_circular);
                holder.delete.setVisibility(View.VISIBLE);
            }
            holder.fenceTitle.setText(bean.getName() + "（直径：" + bean.getRadius() + "米）");
            holder.address.setText(bean.getAddress());
            if ("0".equals(bean.getEnable())) {
                holder.switchbtn.setChecked(true);
            } else {
                holder.switchbtn.setChecked(false);
            }

            holder.switchbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                    if (listener != null) {
                        listener.onOptionClick(index,isCheck);
                    }
                }
            });

            /*

            String date = bean.getRepeat_date();

            int[] chars = new int[7];
            for (int j = 0; j < 7; j++) {
                chars[j] = date.charAt(j);
            }

            int max = 1; // 最大连续的数字个数

            List<Integer> c = new LinkedList<Integer>(); // 连续的子数组

            for (int k = 0; k < chars.length - 1; ++k) {
                if (chars[k] == chars[k + 1]) {
//                    max = max > count ? max : count;
                    c.add(chars[k + 1]);
                } else {
                    if (c.size() > 1) {
                        System.out.println(c);
                    }
                    c.clear();
                    c.add(chars[k + 1]);
                }
            }
*/
            holder.watchTime.setText("监控时段：");

            holder.address.setText(bean.getAddress());
        }

        return view;
    }

    class ViewHolder {
        @Bind(R.id.left_icon)
        ImageView leftIcon;
        @Bind(R.id.left_layout)
        RelativeLayout leftLayout;
        @Bind(R.id.fence_title)
        AppCompatTextView fenceTitle;
        @Bind(R.id.address)
        AppCompatTextView address;
        @Bind(R.id.watch_time)
        AppCompatTextView watchTime;
        @Bind(R.id.delete)
        AppCompatTextView delete;
        @Bind(R.id.switchbtn)
        CheckBox switchbtn;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    OnCheckBoxClickListener listener;

    public void setListener(OnCheckBoxClickListener listener) {
        this.listener = listener;
    }

    public interface OnCheckBoxClickListener {
        void onOptionClick(int index ,boolean isCheck);
    }


    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();

        StringBuilder csb = new StringBuilder();

        String date = "1110111";
        int count = 1;


        char[] chars = new char[7];
        for (int j = 0; j < 7; j++) {
            chars[j] = date.charAt(j);
        }
//        System.out.println(chars);

        List<String> c = new LinkedList<>(); // 连续的子数组
        c.add(chars[0] + "");

        for (int k = 0; k < chars.length - 1; k++) {
            if (chars[k] == chars[k + 1] && chars[k] != '0') {
                c.add(chars[k + 1] + "");
            } else {
                if (c.size() > 2) {
                    System.out.println(c);
                }
                c.clear();
                c.add(chars[k + 1] + "");
            }

            if (chars[k] != '0') {
                count++;
                sb.append(" 周 " + k);
                csb.append(" 周 " + (k + 1));
                if (chars[k] == chars[k + 1]) {
                    count++;
                } else {
                    if (count == 3) {
                        sb.append("至 周 " + k);
                        csb = new StringBuilder();
                    } else {
                        count = 0;
                        sb = new StringBuilder();
                    }
                }
            }
        }

        System.out.println(csb.toString());

        if (c.size() > 2) {
            System.out.println(c);
        }

        System.out.println("end：" + sb.toString());
    }
}
