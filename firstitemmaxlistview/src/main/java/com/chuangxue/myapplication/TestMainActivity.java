package com.chuangxue.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chuangxue.myapplication.widget.view.henrylistview.HenryListView;

import java.util.ArrayList;

/**
 * Created by henry on 2015/2/3.
 */
public class TestMainActivity extends Activity implements HenryListView.HenryListViewListener{
    private HenryListView mListView;
    private FirstItemMaxAdapter mAdapter;
    private int ITEM_HEIGHT;//标准item高,
    private int mScreenWidth = 0;
    private boolean isFisrt = true;
    //刷新相关
    private Handler mHandler;
    private int start = 0;
    private static int refreshCnt = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);
        mListView = (HenryListView) this.findViewById(R.id.firstItemMaxListView);
        WindowManager wm = (WindowManager)this
                .getSystemService(Context.WINDOW_SERVICE);

        mScreenWidth = wm.getDefaultDisplay().getWidth();
        ITEM_HEIGHT = getResources().getDimensionPixelSize(R.dimen.item_height);
        mAdapter = new FirstItemMaxAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setItemHeight(ITEM_HEIGHT);
        mListView.setItemMaxHeight(mScreenWidth);

        mListView.setOnItemClickListener(new AbsListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(TestMainActivity.this, "position:" + position, Toast.LENGTH_SHORT).show();
            }
        });

        //刷新相关
        mHandler = new Handler();
        mListView.setPullLoadEnable(true);
        mListView.setXListViewListener(this);
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                start = ++refreshCnt;
                onLoad();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onLoad();
            }
        }, 2000);
    }

    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime("刚刚");
    }


    class FirstItemMaxAdapter extends BaseAdapter {
        private ArrayList<Item> mDataSources;

        public FirstItemMaxAdapter() {
            mDataSources = new ArrayList<Item>();
            Item item0 = new Item("张一", R.drawable.image1, null);
            Item item1 = new Item("李二", R.drawable.image2, null);
            Item item2 = new Item("张三", R.drawable.image3, null);
            Item item3 = new Item("李四", R.drawable.image4, null);
            Item item4 = new Item("张五", R.drawable.image5, null);
            Item item5 = new Item("李六", R.drawable.image6, null);
            Item item6 = new Item("张七", R.drawable.image7, null);
            Item item7 = new Item("李八", R.drawable.image1, null);
            Item item8 = new Item("张九", R.drawable.image2, null);
            Item item9 = new Item("李十", R.drawable.image3, null);
            Item item10 = new Item("张十一", R.drawable.image4, null);
            Item item11 = new Item("李十二", R.drawable.image5, null);
            Item item12 = new Item("张十三", R.drawable.image6, null);
            Item item13 = new Item("李十四", R.drawable.image7, null);
            Item item14 = new Item("张十五", R.drawable.image1, null);
            Item item15 = new Item("李十六", R.drawable.image2, null);
            Item item16 = new Item("张十七", R.drawable.image3, null);
            Item item17 = new Item("李十八", R.drawable.image4, null);
            Item item18 = new Item("张十九", R.drawable.image5, null);
            Item item19 = new Item("李二十", R.drawable.image6, null);
            Item item20 = new Item("张十一", R.drawable.image4, null);
            Item item21 = new Item("李十二", R.drawable.image5, null);
            Item item22 = new Item("张十三", R.drawable.image6, null);
            Item item23 = new Item("李十四", R.drawable.image7, null);
            Item item24 = new Item("张十五", R.drawable.image1, null);
            Item item25 = new Item("李十六", R.drawable.image2, null);
            Item item26 = new Item("张十七", R.drawable.image3, null);
            Item item27 = new Item("李十八", R.drawable.image4, null);
            Item item28 = new Item("张十九", R.drawable.image5, null);
            Item item29 = new Item("李二十", R.drawable.image6, null);
            Item item30 = new Item("张十一", R.drawable.image4, null);
            Item item31 = new Item("李十二", R.drawable.image5, null);
            Item item32 = new Item("张十三", R.drawable.image6, null);
            Item item33 = new Item("李十四", R.drawable.image7, null);
            Item item34 = new Item("张十五", R.drawable.image1, null);
            Item item35 = new Item("李十六", R.drawable.image2, null);
            Item item36 = new Item("张十七", R.drawable.image3, null);
            Item item37 = new Item("李十八", R.drawable.image4, null);
            Item item38 = new Item("张十九", R.drawable.image5, null);
            Item item39 = new Item("李二十", R.drawable.image6, null);
            Item item40 = new Item("张十一", R.drawable.image4, null);
            Item item41 = new Item("李十二", R.drawable.image5, null);
            Item item42 = new Item("张十三", R.drawable.image6, null);
            Item item43 = new Item("李十四", R.drawable.image7, null);
            Item item44 = new Item("张十五", R.drawable.image1, null);
            Item item45 = new Item("李十六", R.drawable.image2, null);
            Item item46 = new Item("张十七", R.drawable.image3, null);
            Item item47 = new Item("李十八", R.drawable.image4, null);
            Item item48 = new Item("张十九", R.drawable.image5, null);
            Item item49 = new Item("李二十", R.drawable.image6, null);
            mDataSources.add(item0);
            mDataSources.add(item1);
            mDataSources.add(item2);
            mDataSources.add(item3);
            mDataSources.add(item4);
            mDataSources.add(item5);
            mDataSources.add(item6);
            mDataSources.add(item7);
            mDataSources.add(item8);
            mDataSources.add(item9);
            mDataSources.add(item10);
            mDataSources.add(item11);
            mDataSources.add(item12);
            mDataSources.add(item13);
            mDataSources.add(item14);
            mDataSources.add(item15);
            mDataSources.add(item16);
            mDataSources.add(item17);
            mDataSources.add(item18);
            mDataSources.add(item19);
            mDataSources.add(item20);
            mDataSources.add(item21);
            mDataSources.add(item22);
            mDataSources.add(item23);
            mDataSources.add(item24);
            mDataSources.add(item25);
            mDataSources.add(item26);
            mDataSources.add(item27);
            mDataSources.add(item28);
            mDataSources.add(item29);
            mDataSources.add(item30);
            mDataSources.add(item31);
            mDataSources.add(item32);
            mDataSources.add(item33);
            mDataSources.add(item34);
            mDataSources.add(item35);
            mDataSources.add(item36);
            mDataSources.add(item37);
            mDataSources.add(item38);
            mDataSources.add(item39);
            mDataSources.add(item40);
            mDataSources.add(item41);
            mDataSources.add(item42);
            mDataSources.add(item43);
            mDataSources.add(item44);
            mDataSources.add(item45);
            mDataSources.add(item46);
            mDataSources.add(item47);
            mDataSources.add(item48);
            mDataSources.add(item49);
        }

        @Override
        public int getCount() {
            return mDataSources.size();
        }

        @Override
        public Object getItem(int i) {
            return mDataSources.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if(view == null){
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(TestMainActivity.this).inflate(R.layout.first_item_max_item, null);
                viewHolder.cover = (ImageView) view.findViewById(R.id.cover);
                viewHolder.itemLayout = (LinearLayout) view.findViewById(R.id.list_item_layout);
                view.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)view.getTag();
            }
            if (position == 0 && isFisrt) {
                isFisrt = false;
                viewHolder.itemLayout.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, mScreenWidth));
            } else {
                viewHolder.itemLayout.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, ITEM_HEIGHT));
            }
            viewHolder.cover.setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewHolder.cover.setImageResource(mDataSources.get(position).imgId);
            return view;
        }

        class ViewHolder {
            LinearLayout itemLayout;
            TextView name;
            ImageView cover;
        }
    }

    class Item {
        String name;
        int imgId;
        String videoUrl;

        Item(String name, int imgId, String videoUrl) {
            this.name = name;
            this.imgId = imgId;
            this.videoUrl = videoUrl;
        }
    }
}
