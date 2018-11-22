package com.cds.iot.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.data.Constant;
import com.cds.iot.data.entity.MessageType;
import com.cds.iot.module.adapter.OptionAdapter;
import com.cds.iot.util.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * 弹窗视图
 */
public class MenuPopWindow extends PopupWindow implements View.OnClickListener, AdapterView.OnItemClickListener {
    public static final String TAG = "MenuPopWindow";
    private Context context;

    View all_message;

    TextView allMessageTv;

    ListView listView;

    OptionAdapter adapter;

    int select;

    public MenuPopWindow setSelect(int select) {
        this.select = select;
        if (select == -1) {
            allMessageTv.setTextColor(context.getResources().getColor(R.color.theme_color));
        } else {
            allMessageTv.setTextColor(context.getResources().getColor(R.color.popwindow_option_text));
        }
        adapter.setSelect(select);
        return this;
    }

    public void setDataList(List<MessageType> mDataList) {
        adapter.setDataList(mDataList);
    }

    public MenuPopWindow(Context context) {
        super(context);
        this.context = context;
        initalize();
    }

    private void initalize() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_message_menu, null);
        all_message = view.findViewById(R.id.all_message);//发起群聊
        all_message.setOnClickListener(this);
        allMessageTv = view.findViewById(R.id.all_message_tv);
        listView = view.findViewById(R.id.listview);
        adapter = new OptionAdapter(context);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        setContentView(view);
        initWindow();
    }

    private void initWindow() {
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        this.setWidth((int) (d.widthPixels * 0.3));
        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        backgroundAlpha((Activity) context, 0.9f);//0.0-1.0
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha((Activity) context, 1f);
            }
        });
    }

    //设置添加屏幕的背景透明度
    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    public MenuPopWindow showAtBottom(View view) {
        Logger.i(TAG, "width：" + getWidth() + " height：" + getHeight());
        //弹窗位置设置
        showAsDropDown(view, -200, 20);
        //showAtLocation(view, Gravity.TOP | Gravity.RIGHT, 10, 110);//有偏差
        return this;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.all_message:
                if (listener != null) {
                    listener.onOptionClick(-1, -1);
                }
                dismiss();
                break;
            default:
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (listener != null) {
            int typeId = adapter.getDataList().get(i).getMessage_tyep_id();
            listener.onOptionClick(i, typeId);
        }
        dismiss();
    }

    OnOptionClickListener listener;

    public void setListener(OnOptionClickListener listener) {
        this.listener = listener;
    }

    public interface OnOptionClickListener {
        void onOptionClick(int index, int typeId);
    }
}

