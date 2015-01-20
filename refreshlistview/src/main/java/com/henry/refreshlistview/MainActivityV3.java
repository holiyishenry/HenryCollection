package com.henry.refreshlistview;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.henry.refreshlistview.widget.view.ReFlashListView;
import com.henry.refreshlistview.widget.view.UpAndDownReFlashListView;

import java.util.ArrayList;

/**
 * 测试header下拉
 * @author holiy
 *
 */
public class MainActivityV3 extends Activity implements UpAndDownReFlashListView.OnHeaderReflashListener, UpAndDownReFlashListView.OnFooterReflashListener{
    ArrayList<ApkEntity> apk_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_v3);
        setData();
        showList(apk_list);
    }

    MyAdapter adapter;
    UpAndDownReFlashListView listview;
    private void showList(ArrayList<ApkEntity> apk_list) {
        if (adapter == null) {
            listview = (UpAndDownReFlashListView)findViewById(R.id.listview);
            listview.setOnHeaderReflashListener(this);
            listview.setOnFooterReflashListener(this);

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
    public void onFooterReflash() {
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
                listview.reflashComplete(false);
            }
        }, 2000);
    }

    @Override
    public void onHeaderReflash() {
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
                listview.reflashComplete(true);
            }
        }, 2000);
    }
}
