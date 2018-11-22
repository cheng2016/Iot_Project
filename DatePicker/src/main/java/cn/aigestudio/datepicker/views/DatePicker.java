package cn.aigestudio.datepicker.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.aigestudio.datepicker.bizs.decors.DPDecor;
import cn.aigestudio.datepicker.bizs.languages.DPLManager;
import cn.aigestudio.datepicker.bizs.themes.DPTManager;
import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.utils.MeasureUtil;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * DatePicker
 *
 * @author AigeStudio 2015-06-29
 */
public class DatePicker extends LinearLayout {
    private DPTManager mTManager;// 主题管理器
    private DPLManager mLManager;// 语言管理器

    private MonthView monthView;// 月视图
    private TextView tvYear, tvMonth;// 年份 月份显示
    private TextView tvEnsure;// 确定按钮显示

    LinearLayout rlTitle;

    LinearLayout llWeek;

    private OnDateSelectedListener onDateSelectedListener;// 日期多选后监听

    /**
     * 日期单选监听器
     */
    public interface OnDatePickedListener {
        void onDatePicked(String date);
    }

    /**
     * 日期多选监听器
     */
    public interface OnDateSelectedListener {
        void onDateSelected(List<String> date);
    }

    public DatePicker(Context context) {
        this(context, null);
    }

    public DatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTManager = DPTManager.getInstance();
        mLManager = DPLManager.getInstance();

        // 设置排列方向为竖向
        setOrientation(VERTICAL);

        LayoutParams params =
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        int margin = MeasureUtil.dp2px(context,10);
        params.setMargins(0,margin,0,0);
        int padding = MeasureUtil.dp2px(context,10);
        setPadding(0,0,0,padding);
        setLayoutParams(params);

        LayoutParams llParams =
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        // 标题栏根布局
        rlTitle = new LinearLayout(context);
        rlTitle.setOrientation(HORIZONTAL);
        rlTitle.setGravity(Gravity.CENTER);
        rlTitle.setBackgroundColor(mTManager.colorTitleBG());
        int rlTitlePadding = MeasureUtil.dp2px(context, 12);
        rlTitle.setPadding(rlTitlePadding, rlTitlePadding, rlTitlePadding, rlTitlePadding);

        // 周视图根布局
        llWeek = new LinearLayout(context);
        llWeek.setBackgroundColor(mTManager.colorWeekTitleBG());
        llWeek.setOrientation(HORIZONTAL);
        int llWeekPadding = MeasureUtil.dp2px(context, 8);
        llWeek.setPadding(0, llWeekPadding, 0, llWeekPadding);
        LayoutParams lpWeek = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpWeek.weight = 1;

        // 标题栏子元素布局参数
        RelativeLayout.LayoutParams lpYear =
                new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpYear.addRule(RelativeLayout.CENTER_VERTICAL);
        RelativeLayout.LayoutParams lpMonth =
                new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpMonth.addRule(RelativeLayout.CENTER_IN_PARENT);
        RelativeLayout.LayoutParams lpEnsure =
                new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpEnsure.addRule(RelativeLayout.CENTER_VERTICAL);
        lpEnsure.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        // --------------------------------------------------------------------------------标题栏
        // 年份显示
        tvYear = new TextView(context);
//        int p = MeasureUtil.dp2px(context, 10);
        tvYear.setText("2015");
//        tvYear.setPadding(p,p,p,0);
        tvYear.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        tvYear.setTextColor(mTManager.colorTitle());

        // 月份显示
        tvMonth = new TextView(context);
//        tvMonth.setPadding(p,p,p,0);
        tvMonth.setText("六月");
        tvMonth.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        tvMonth.setTextColor(mTManager.colorTitle());

        // 确定显示
        tvEnsure = new TextView(context);
        tvEnsure.setText(mLManager.titleEnsure());
        tvEnsure.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        tvEnsure.setTextColor(mTManager.colorTitle());
        tvEnsure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onDateSelectedListener) {
                    onDateSelectedListener.onDateSelected(monthView.getDateSelected());
                }
            }
        });

        rlTitle.addView(tvYear, lpYear);
        rlTitle.addView(tvMonth, lpMonth);
        rlTitle.addView(tvEnsure, lpEnsure);

        addView(rlTitle, llParams);

        // --------------------------------------------------------------------------------周视图
        for (int i = 0; i < mLManager.titleWeek().length; i++) {
            TextView tvWeek = new TextView(context);
            tvWeek.setText(mLManager.titleWeek()[i]);
            tvWeek.setGravity(Gravity.CENTER);
            tvWeek.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            tvWeek.setTextColor(mTManager.colorWeekTitle());
            tvWeek.setHeight(100);
            llWeek.addView(tvWeek, lpWeek);
        }
        addView(llWeek, llParams);

        // ------------------------------------------------------------------------------------月视图
        monthView = new MonthView(context);
        monthView.setOnDateChangeListener(new MonthView.OnDateChangeListener() {
            @Override
            public void onMonthChange(int month) {
                tvMonth.setText(mLManager.titleMonth()[month - 1]);
            }

            @Override
            public void onYearChange(int year) {
                String tmp = String.valueOf(year);
                if (tmp.startsWith("-")) {
                    tmp = tmp.replace("-", mLManager.titleBC());
                }
                tvYear.setText(tmp+"年");
            }
        });
        addView(monthView, llParams);
    }

    public void hideYmTitleView(){
        removeView(rlTitle);
    }

    public void setTitleBackground(int res){
        rlTitle.setBackgroundResource(res);
    }

    public void setWeekBackground(int res){
        llWeek.setBackgroundResource(res);
    }

    public void setBottomBackground(int res){
        monthView.setBackgroundResource(res);
    }

    /**
     * 设置初始化年月日期
     *
     * @param year  ...
     * @param month ...
     */
    public void setDate(int year, int month) {
        if (month < 1) {
            month = 1;
        }
        if (month > 12) {
            month = 12;
        }
        monthView.setDate(year, month);
    }

    public void setDPDecor(DPDecor decor) {
        monthView.setDPDecor(decor);
    }

    /**
     * 设置日期选择模式
     *
     * @param mode ...
     */
    public void setMode(DPMode mode) {
        if (mode != DPMode.MULTIPLE) {
            tvEnsure.setVisibility(GONE);
        }
        monthView.setDPMode(mode);
    }

    public void setFestivalDisplay(boolean isFestivalDisplay) {
        monthView.setFestivalDisplay(isFestivalDisplay);
    }

    public void setTodayDisplay(boolean isTodayDisplay) {
        monthView.setTodayDisplay(isTodayDisplay);
    }

    public void setHolidayDisplay(boolean isHolidayDisplay) {
        monthView.setHolidayDisplay(isHolidayDisplay);
    }

    public void setDeferredDisplay(boolean isDeferredDisplay) {
        monthView.setDeferredDisplay(isDeferredDisplay);
    }

    /**
     * 设置单选监听器
     *
     * @param onDatePickedListener ...
     */
    public void setOnDatePickedListener(OnDatePickedListener onDatePickedListener) {
        if (monthView.getDPMode() != DPMode.SINGLE) {
            throw new RuntimeException(
                    "Current DPMode does not SINGLE! Please call setMode set DPMode to SINGLE!");
        }
        monthView.setOnDatePickedListener(onDatePickedListener);
    }

    /**
     * 设置多选监听器
     *
     * @param onDateSelectedListener ...
     */
    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        if (monthView.getDPMode() != DPMode.MULTIPLE) {
            throw new RuntimeException(
                    "Current DPMode does not MULTIPLE! Please call setMode set DPMode to MULTIPLE!");
        }
        this.onDateSelectedListener = onDateSelectedListener;
    }
}
