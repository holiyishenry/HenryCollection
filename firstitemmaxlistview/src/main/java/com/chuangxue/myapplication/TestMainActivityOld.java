package com.chuangxue.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuangxue.myapplication.widget.view.HenryOldListView;

import java.util.ArrayList;

/**
 * Created by henry on 2015/2/3.
 */
public class TestMainActivityOld extends Activity {
    private HenryOldListView mListView;
    private FirstItemMaxAdapter mAdapter;
    private int ITEM_HEIGHT;//标准item高,
    private int mScreenWidth = 0;
    private boolean isFisrt = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);
        mListView = (HenryOldListView) this.findViewById(R.id.firstItemMaxListView);
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
                Toast.makeText(TestMainActivityOld.this, "position:" + position, Toast.LENGTH_SHORT).show();
            }
        });
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
            view = LayoutInflater.from(TestMainActivityOld.this).inflate(R.layout.first_item_max_item, null);
            viewHolder = new ViewHolder();
            if (position == 0 && isFisrt) {
                isFisrt = false;
                view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, mScreenWidth));
            } else {
                view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, ITEM_HEIGHT));
            }
            viewHolder.cover = (ImageView) view.findViewById(R.id.cover);

            viewHolder.cover.setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewHolder.cover.setImageResource(mDataSources.get(position).imgId);
            return view;
        }

        class ViewHolder {
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
