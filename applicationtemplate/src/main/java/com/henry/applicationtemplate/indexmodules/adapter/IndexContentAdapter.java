package com.henry.applicationtemplate.indexmodules.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;

import com.henry.applicationtemplate.R;
import com.henry.applicationtemplate.common.storageutils.SharedPreferencesUtils;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Henry on 2014/12/21.
 * 首页内容Adapter
 */
public class IndexContentAdapter extends BaseAdapter{
    private List<String> sourceList;
    private LayoutInflater layoutInflater;
    private Context context;
    public ArrayList<View> cacheView;
    SharedPreferencesUtils sharedPreferencesUtils;

    public IndexContentAdapter(Context context, List<String> sourceList){
        this.sourceList = sourceList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        cacheView = new ArrayList<View>();
        sharedPreferencesUtils = new SharedPreferencesUtils(context,"adapter");
    }
    @Override
    public int getCount() {
        return sourceList.size();
    }

    @Override
    public Object getItem(int position) {
        return sourceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ItemView{
        public LinearLayout menuLinearLayout;
        public LinearLayout hideLinearLayout;
        public Button openButton;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemView itemView;
        if(convertView == null){
            itemView = new ItemView();
            convertView = layoutInflater.inflate(R.layout.index_list_item,null);
            itemView.menuLinearLayout = (LinearLayout)convertView.findViewById(R.id.item_menu);
            itemView.openButton = (Button)convertView.findViewById(R.id.open_menu_btn);
            itemView.hideLinearLayout = (LinearLayout)convertView.findViewById(R.id.hide_layout);
            convertView.setTag(itemView);
        }else{
            itemView = (ItemView)convertView.getTag();
        }

        itemView.openButton.setOnClickListener(new ClickListener(convertView ,itemView.menuLinearLayout, itemView.hideLinearLayout));

        return convertView;
    }

    private class ClickListener implements View.OnClickListener{
        View view, hideView;
        View convertView;
        ClickListener( View convertView,View view, View hideView)
        {
            this.convertView = convertView;
            this.view = view;
            this.hideView = hideView;
        }
        @Override
        public void onClick(View v) {
            if(!view.isSelected()){
                sharedPreferencesUtils.put("positon","true");
                cacheView.add(convertView);
                openMenu(view, hideView, 500);
            }else{
                sharedPreferencesUtils.put("positon","false");
                closeMenu(view, hideView, 500);
            }
        }
    }


    public void openMenu( View menu, View hideView, int time){
        if(!menu.isSelected()){
            menu.setSelected(true);
            hideView.setVisibility(View.VISIBLE);
            openHideView(hideView,time);
            openMenuTranslation(menu, time);
        }
    }

    public void closeMenu(View menu, View hideView, int time){
        if(menu.isSelected()){
            menu.setSelected(false);
            closeMenuTranslation(menu, time);
            closeHideView(hideView, time);
        }
    }

    /**
     * 打开菜单
     * @param view
     */
    private void openHideView(final View view, final int time){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ViewGroup.LayoutParams params = view.getLayoutParams();
                for(int i= 0; i < time / 10; i++){
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            params.height += 2;
                            view.setLayoutParams(params);
                        }
                    });
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 关闭菜单
     */
    public void closeHideView(final View view, final int time){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ViewGroup.LayoutParams params = view.getLayoutParams();
                for(int i= 0; i < time / 10; i++){
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            params.height -= 2;
                            view.setLayoutParams(params);
                        }
                    });
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 打开菜单动画
     * @param view
     * @param time
     */
    private void openMenuTranslation(View view, int time){
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(view, "translationX", view.getLeft(), view.getLeft()),
                ObjectAnimator.ofFloat(view, "translationY", 0, 100));
        set.setDuration(time).start();
    }

    /**
     * 关闭菜单动画
     * @param view
     * @param time
     */
    public void closeMenuTranslation(View view, int time){
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(view, "translationX", view.getLeft(), view.getLeft()),
                ObjectAnimator.ofFloat(view, "translationY", 100, 0));
        set.setDuration(time).start();
    }

}
