package com.henry.refreshlistview;

import java.util.ArrayList;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;

import com.henry.refreshlistview.widget.view.DownReflashListView;

/**
 * 测试footer上啦
 * @author holiy
 *
 */
public class MainActivityV2 extends Activity implements DownReflashListView.FooterReflashListener {
    ArrayList<ApkEntity> apk_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_v2);
        setData();
        showList(apk_list);
    }

    MyAdapter adapter;
    DownReflashListView listview;
    private void showList(ArrayList<ApkEntity> apk_list) {
        if (adapter == null) {
            listview = (DownReflashListView)findViewById(R.id.footer_reflash_listview);
            listview.setInterface(this);
            adapter = new MyAdapter(this, apk_list);
            listview.setAdapter(adapter);
        } else {
            adapter.onDateChange(apk_list);
        }
    }

    private void setData() {
        apk_list = new ArrayList<ApkEntity>();
        for (int i = 0; i < 10; i++) {
            ApkEntity entity = new ApkEntity();
            entity.setName("默认数据");
            entity.setDes("这是一个神奇的应用");
            entity.setInfo("50w用户");
            apk_list.add(entity);
        }
    }

    private void setReflashData() {
        for (int i = 0; i < 2; i++) {
            ApkEntity entity = new ApkEntity();
            entity.setName("刷新数据");
            entity.setDes("这是一个神奇的应用");
            entity.setInfo("50w用户");
            apk_list.add(0,entity);
        }
    }
    @Override
    public void onReflash() {
        // TODO Auto-generated method stub\
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                //获取最新数据
                setReflashData();
                //通知界面显示
                showList(apk_list);
                //通知listview 刷新数据完毕；
                listview.reflashComplete();
            }
        }, 2000);

    }

}
