package com.cds.iot.module.manage.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.base.BaseActivity;
import com.cds.iot.data.entity.DeviceUser;
import com.cds.iot.util.picasso.PicassoCircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * ,
 */
public class ManageUserActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    @Bind(R.id.right_tv)
    AppCompatTextView rightTv;
    @Bind(R.id.list_view)
    ListView listView;

    UserAdapter adapter;
    Integer selectItem;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manage_user;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((TextView) findViewById(R.id.title)).setText("选择管理员");
        findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.right_button).setVisibility(View.VISIBLE);
        rightTv.setVisibility(View.INVISIBLE);
        rightTv.setTextColor(getResources().getColor(R.color.theme_color));
        rightTv.setOnClickListener(this);
        rightTv.setText("完成");
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            List<DeviceUser> users = (List<DeviceUser>) getIntent().getExtras().getSerializable("users");
            users = users.subList(1,users.size());
            adapter = new UserAdapter(users);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.right_tv:
                Intent intent = new Intent();
                intent.putExtra("userid", adapter.getDataList().get(selectItem).getId());
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        rightTv.setVisibility(View.VISIBLE);
        selectItem = position;
        adapter.notifyDataSetChanged();
    }

    class UserAdapter extends BaseAdapter {
        List<DeviceUser> mDataList;

        public UserAdapter(List<DeviceUser> mDataList) {
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

        @Override
        public View getView(int index, View view, ViewGroup parent) {
            ViewHolder holder;
            if (view == null || view.getTag() == null) {
                view = LayoutInflater.from(ManageUserActivity.this).inflate(R.layout.item_device_user, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            DeviceUser bean = mDataList.get(index);
            holder.nickNameTv.setText(bean.getName());
            if (!TextUtils.isEmpty(bean.getImg_url())) {
                Picasso.with(ManageUserActivity.this)
                        .load(bean.getImg_url())
                        .placeholder(R.mipmap.btn_male_big)
                        .transform(new PicassoCircleTransform())
                        .into(holder.headImg);
            }

            if (selectItem != null && selectItem == index) {
                holder.selectImg.setVisibility(View.VISIBLE);
            } else {
                holder.selectImg.setVisibility(View.INVISIBLE);
            }
            return view;
        }
    }

    static class ViewHolder {
        @Bind(R.id.head_img)
        ImageView headImg;
        @Bind(R.id.nick_name_tv)
        TextView nickNameTv;
        @Bind(R.id.select_img)
        ImageView selectImg;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
