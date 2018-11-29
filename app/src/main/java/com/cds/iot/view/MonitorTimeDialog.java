package com.cds.iot.view;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cds.iot.R;
import com.cds.iot.util.Logger;
import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Chengzj
 * @date 2018/9/7 10:30
 * <p>
 * 监控时间对话框
 */
public class MonitorTimeDialog extends Dialog implements AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String TAG = "MonitorTimeDialog";

    private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm");//设置日期格式

    protected Context mContext;

    protected WindowManager.LayoutParams mLayoutParams;

    private Button cancelBtn, confirmBtn;

    private TextView titleTv, timeLengthTv;

    private TextView startTv, endTv;

    private GridView weekGridView;

    WeekAdapter weekAdapter;

    Set<Integer> mSelect = new HashSet<>();

    String startStr;

    String endStr;


    public interface OnMonitorClickListener {
        void onOnMonitorClick(Set<Integer> selectPosSet, String start, String end);
    }

    public MonitorTimeDialog(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public MonitorTimeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    protected MonitorTimeDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView(context);
    }

    private MonitorTimeDialog initView(Context context) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawableResource(R.mipmap.transparent_bg);
        mContext = context;
        Window window = this.getWindow();
        mLayoutParams = window.getAttributes();
        mLayoutParams.alpha = 1f;
        window.setAttributes(mLayoutParams);
        if (mLayoutParams != null) {
            mLayoutParams.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
            mLayoutParams.gravity = Gravity.CENTER;
        }
        mContext = context;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_monitor_dialog, null);
        titleTv = (TextView) dialogView.findViewById(R.id.title);
        startTv = (TextView) dialogView.findViewById(R.id.start);
        endTv = (TextView) dialogView.findViewById(R.id.end);

        startTv.setOnClickListener(this);
        endTv.setOnClickListener(this);

        timeLengthTv = (TextView) dialogView.findViewById(R.id.time_length);

        weekGridView = (GridView) dialogView.findViewById(R.id.week);
        weekGridView.setOnItemClickListener(this);
        weekAdapter = new WeekAdapter();
        weekGridView.setAdapter(weekAdapter);

        confirmBtn = (Button) dialogView.findViewById(R.id.confirm);
        cancelBtn = (Button) dialogView.findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
//        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(dialogView);

        return this;
    }

    public MonitorTimeDialog setTitle(String message) {
        titleTv.setVisibility(View.VISIBLE);
        titleTv.setText(message);
        return this;
    }

    public MonitorTimeDialog setTime(String start, String end) {
        startStr = TextUtils.isEmpty(start) ? "00:00" : start;
        endStr = TextUtils.isEmpty(end) ? "23:59" : end;
        startTv.setText(startStr);
        endTv.setText(endStr);
        caculateLength();
        return this;
    }

    public MonitorTimeDialog setSelectWeek(String week) {
        for (int i = 0; i < 7; i++) {
            if ("1".equals(week.substring(i, i + 1))) {
                mSelect.add(i);
            }
        }
        return this;
    }

    public MonitorTimeDialog setPositiveButton(String str, OnMonitorClickListener listener) {
        confirmBtn.setText(str);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onOnMonitorClick(mSelect, startTv.getText().toString(), endTv.getText().toString());
                dismiss();
            }
        });
        return this;
    }


    public MonitorTimeDialog setCancelButton(String str, final View.OnClickListener listener) {
        cancelBtn.setText(str);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view);
                dismiss();
            }
        });
        return this;
    }

    public MonitorTimeDialog showDialog() {
        show();
        return this;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView dayTv = (TextView) view.findViewById(R.id.dayTv);
        if (dayTv.isSelected()) {
            dayTv.setSelected(false);
            mSelect.remove(i);
        } else {
            mSelect.add(i);
            dayTv.setSelected(true);
        }
        Logger.i(TAG, "onItemClick index：" + i + " mSelect：" + new Gson().toJson(mSelect));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                showTimePickerDialog(startTv);
                break;
            case R.id.end:
                showTimePickerDialog(endTv);
                break;
            default:
                break;
        }
    }

    void showTimePickerDialog(TextView textView) {
        int hour = Integer.parseInt(textView.getText().toString().substring(0, 2));
        int minute = Integer.parseInt(textView.getText().toString().substring(3, 5));
        new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                DateTime dateTime = new DateTime().withHourOfDay(hourOfDay).withMinuteOfHour(minute);
                textView.setText(format.format(dateTime.toDate()));
                caculateLength();
            }
        }, hour, minute, true).show();
    }

    void caculateLength() {
        int startHour = Integer.parseInt(startTv.getText().toString().substring(0, 2));
        int startMinute = Integer.parseInt(startTv.getText().toString().substring(3, 5));
        int endHour = Integer.parseInt(endTv.getText().toString().substring(0, 2));
        int endMinute = Integer.parseInt(endTv.getText().toString().substring(3, 5));
        //joda-time
        DateTime start = new DateTime().withHourOfDay(startHour).withMinuteOfHour(startMinute);// 1:30:26PM
        DateTime end = new DateTime().withHourOfDay(endHour).withMinuteOfHour(endMinute);// 1:30:26PM
        Period period = new Period(start, end);
        int hours = period.getHours();
        int minutes = period.getMinutes();
        timeLengthTv.setText("监控时长：" + Math.abs(hours) + " 小时" + Math.abs(minutes) + "分钟");
        Logger.i(TAG, "hour：" + hours + " minutes：" + minutes);
    }

    class WeekAdapter extends BaseAdapter {
        String[] strings = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

        public WeekAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return strings.length;
        }

        @Override
        public Object getItem(int i) {
            return strings[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder holder;
            if (convertView == null || convertView.getTag() == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_week, null, false);
                holder.dayTv = (TextView) convertView.findViewById(R.id.dayTv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.dayTv.setSelected(false);
            if (mSelect.size() > 0) {
                for (Integer integer : mSelect) {
                    if (integer == i) {
                        holder.dayTv.setSelected(true);
                    }
                }
            }
            holder.dayTv.setText(strings[i]);
            return convertView;
        }

        class ViewHolder {
            TextView dayTv;
        }


    }

    public static void main(String[] args) {
        String str = "09 : 08";

        System.out.println(str.substring(0, 2));

        System.out.println(str.substring(5, 7));

        LocalTime start = new LocalTime(14, 45, 0, 0);// 1:30:26PM
        LocalTime end = new LocalTime(15, 30, 0, 0);// 1:30:26PM

        int hour = Hours.hoursBetween(start, end).getHours();
        System.out.println("hour：" + hour);
        int minutes = Minutes.minutesBetween(start, end).getMinutes();
        System.out.println("minutes：" + minutes);

        System.out.println("================================");

        Period p = new Period(start, end, PeriodType.hours());
        hour = p.getHours();
        System.out.println("hour：" + hour);

        p = new Period(start, end, PeriodType.minutes());
        minutes = p.getMinutes();
        minutes = 295;
        System.out.println("minutes：" + minutes % 60);
        System.out.println("================================");
        String a = "08:54";
        System.out.println("hour：" + a.substring(0, 2));
        System.out.println("minutes：" + a.substring(3, 5));
    }
}
