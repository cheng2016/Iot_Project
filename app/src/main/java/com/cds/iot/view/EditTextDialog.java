package com.cds.iot.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.util.ToastUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditTextDialog extends Dialog {

    protected Context mContext;

    protected WindowManager.LayoutParams mLayoutParams;

    private Button cancelBtn, confirmBtn;

    private EditText nameEdit, phoneEdit;

    private TextView contactTv, titleTv;

    public EditTextDialog(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public EditTextDialog(@NonNull Context context, String name, String phone) {
        super(context);
        initView(context);
        setNameEdit(name);
        setPhoneEdit(phone);
    }

    public EditTextDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    protected EditTextDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawableResource(R.mipmap.transparent_bg);
        Window window = this.getWindow();
        mLayoutParams = window.getAttributes();
        mLayoutParams.alpha = 1f;
        window.setAttributes(mLayoutParams);
        mLayoutParams.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        mLayoutParams.gravity = Gravity.CENTER;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_choose, null);
        nameEdit = (EditText) dialogView.findViewById(R.id.name);
        phoneEdit = (EditText) dialogView.findViewById(R.id.phone);
        contactTv = (TextView) dialogView.findViewById(R.id.contact);
        titleTv = (TextView) dialogView.findViewById(R.id.title_tv);
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
        InputFilter[] emojiFilters = {new InputFilter.LengthFilter(10),emojiFilter};
        nameEdit.setFilters(emojiFilters);
    }

    InputFilter emojiFilter = new InputFilter() {
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher emojiMatcher = emoji.matcher(source);
            if (emojiMatcher.find()) {
                ToastUtils.showShort(mContext,"不支持输入表情");
                return "";
            }
            return null;
        }
    };

    public void setNameEdit(String message) {
        nameEdit.setText(message);
    }

    public String getNameText() {
        return nameEdit.getText().toString().trim();
    }

    public String getPhoneText() {
        return phoneEdit.getText().toString().trim();
    }

    public void setPhoneEdit(String message) {
        phoneEdit.setText(message);
    }


    public void setTitle(String msg) {
        titleTv.setText(msg);
    }

    public EditTextDialog setPositiveButton(final View.OnClickListener listener) {
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view);
            }
        });
        return this;
    }

    public EditTextDialog setContactButton(final View.OnClickListener listener) {
        contactTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view);
            }
        });
        return this;
    }

    public EditTextDialog showDialog(){
        show();
        return this;
    }
}