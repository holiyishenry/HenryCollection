package com.holiy.topsidemenu;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.holiy.R;
import com.holiy.topsidemenu.widget.view.MenuDrawer;
import com.holiy.topsidemenu.widget.view.Position;

import java.util.ArrayList;

public class ComplexTestActivity extends Activity {
    private MenuDrawer mMenuDrawer;
    private ListView listView;
    private MyAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMenuDrawer = MenuDrawer.attach(this, Position.TOP);
        mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_NONE);
        mMenuDrawer.setContentView(R.layout.activity_right_siding);
        mMenuDrawer.setMenuView(R.layout.menu_top);
        mMenuDrawer.setMenuSize(1000);
        listView = (ListView) findViewById(R.id.list);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
    }

    public void toggleMenu(View view){
        mMenuDrawer.toggleMenu();
    }

    class MyAdapter extends BaseAdapter {
        private ArrayList<String> mDataSources;

        public MyAdapter() {
            mDataSources = new ArrayList<String>();
            for(int i = 0; i < 20; i++){
                mDataSources.add("a" + i);
            }
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
                view = LayoutInflater.from(ComplexTestActivity.this).inflate(R.layout.right_menu_layout, null);
                view.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)view.getTag();
            }
            return view;
        }

        class ViewHolder {
            RelativeLayout itemLayout;
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
