package com.henry.imagegallery.finishbyHorizontalListview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.henry.imagegallery.R;

public class MainActivity extends Activity {
    HorizontalListView hListView;
    HorizontalListViewAdapter hListViewAdapter;
    ImageView previewImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_hlistview);
        initUI();
    }

    public void initUI() {
        hListView = (HorizontalListView) findViewById(R.id.horizon_listview);
        previewImg = (ImageView) findViewById(R.id.image_preview);
        String[] titles = {"开心", "无奈", "伤心", "幸福", "赞", "生气"};
        final int[] ids = {R.drawable.emo_baobao_01, R.drawable.emo_baobao_02,
                R.drawable.emo_baobao_03, R.drawable.emo_baobao_04,
                R.drawable.emo_baobao_05, R.drawable.emo_baobao_06};
        hListViewAdapter = new HorizontalListViewAdapter(getApplicationContext(), titles, ids);
        hListView.setAdapter(hListViewAdapter);
        hListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
//				if(olderSelectView == null){
//					olderSelectView = view;
//				}else{
//					olderSelectView.setSelected(false);
//					olderSelectView = null;
//				}
//				olderSelectView = view;
//				view.setSelected(true);
                previewImg.setImageResource(ids[position]);
                hListViewAdapter.setSelectIndex(position);
                hListViewAdapter.notifyDataSetChanged();

            }
        });
    }
}