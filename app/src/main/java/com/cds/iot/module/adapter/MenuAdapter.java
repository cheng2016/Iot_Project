package com.cds.iot.module.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cds.iot.R;
import com.cds.iot.data.entity.BindScenes;
import com.cds.iot.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends BaseAdapter {
    public final static String TAG = "MenuAdapter";
    private Context context;
    private List<BindScenes> mDataList = new ArrayList<>();
    private int selectItem = -1;

    public MenuAdapter(Context context) {
        this.context = context;
    }

    public List<BindScenes> getDataList() {
        return mDataList;
    }

    public void setDataList(List<BindScenes> mDataList) {
        this.mDataList = mDataList;
        notifyDataSetChanged();
    }

    public void setDataListAndCalHeight(List<BindScenes> listItems, ListView listView) {
        this.mDataList = listItems;
        this.notifyDataSetChanged();
        Utils.calListViewWidthAndHeight(listView);
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

    public int getSelectItem() {
        return selectItem;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_scense_menu, null);
            holder.nameTv = (TextView) convertView.findViewById(R.id.menu_name_tv);
            holder.menuImg = (ImageView) convertView.findViewById(R.id.menu_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (selectItem == index) {
            holder.nameTv.setSelected(true);
            holder.menuImg.setSelected(true);
        } else {
            holder.nameTv.setSelected(false);
            holder.menuImg.setSelected(false);
        }

        if (!mDataList.isEmpty()) {
            final BindScenes bean = mDataList.get(index);
            holder.nameTv.setText(bean.getName());
            if(bean.getId().equals("1")){
                holder.menuImg.setImageDrawable(context.getResources().getDrawable(R.drawable.menu_car_selector));
            }else if(bean.getId().equals("2")){
                holder.menuImg.setImageDrawable(context.getResources().getDrawable(R.drawable.menu_man_selector));
            }else if(bean.getId().equals("3")){
                holder.menuImg.setImageDrawable(context.getResources().getDrawable(R.drawable.menu_house_selector));
            }

//          Picasso.with(context).load(bean.getIcon_url()).into(holder.menuImg);

/*            Observable.create(new ObservableOnSubscribe<Drawable>() {
                @Override
                public void subscribe(ObservableEmitter<Drawable> emitter) throws Exception {
                    Bitmap normalBitmap = Picasso.with(context).load(bean.getIcon_url()).get();
                    Bitmap selectedBitmap = Picasso.with(context).load(bean.getIcon_hl_url()).get();
                    Drawable drawable = SelectorUtils.generatePressedSelector(new BitmapDrawable(selectedBitmap), new BitmapDrawable(normalBitmap));
                    Logger.d(TAG, "After observeOn(io), current thread is: " + Thread.currentThread().getName());
                    emitter.onNext(drawable);
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Drawable>(){
                        @Override
                        public void accept(Drawable drawable) throws Exception {
                            Logger.d(TAG, "After observeOn(mainThread), current thread is : " + Thread.currentThread().getName());
                            holder.menuImg.setImageDrawable(drawable);
                        }
                    });*/
            /*new AsyncTask<Void,Void,Drawable>(){
                @Override
                protected Drawable doInBackground(Void... voids) {
                    try {
                        Bitmap normalBitmap = Picasso.with(context).load(bean.getIcon_url()).get();
                        Bitmap selectedBitmap = Picasso.with(context).load(bean.getIcon_hl_url()).get();
                        Drawable drawable = SelectorUtils.generatePressedSelector(new BitmapDrawable(selectedBitmap), new BitmapDrawable(normalBitmap));
                        return drawable;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Drawable drawable) {
                    super.onPostExecute(drawable);
                    holder.menuImg.setImageDrawable(drawable);
                }
            }.execute();*/
        }
        return convertView;
    }

    class ViewHolder {
        TextView nameTv;
        ImageView menuImg;
    }
}
