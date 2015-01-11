package com.henry.applicationtemplate.indexmodules;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.henry.applicationtemplate.R;
import com.henry.applicationtemplate.common.MyLog;
import com.henry.applicationtemplate.common.storageutils.SDCardUtils;
import com.henry.applicationtemplate.indexmodules.adapter.IndexContentAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author henry
 * 首页Fragment
 */
public class IndexTabFragment extends Fragment {
    private ListView indexContentListView;
    private IndexContentAdapter indexContentAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_tab_index, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setListener();
        MyLog.i("SDCard", "sdcard:" + SDCardUtils.getSDCardPath());
        MyLog.i("root","root:" + SDCardUtils.getDataDirectoryPath());
    }

    private void initView(){
        indexContentListView = (ListView)getActivity().findViewById(R.id.index_content_listview);
        List<String> datas = new ArrayList<String>();
        for(int i = 0; i < 10; i++){
            datas.add("1");
        }
        indexContentAdapter = new IndexContentAdapter(getActivity(), datas);
        indexContentListView.setAdapter(indexContentAdapter);
    }

    private void setListener(){
        indexContentListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == SCROLL_STATE_TOUCH_SCROLL){//listview正在滚动
                    if(indexContentAdapter.cacheView.size() > 0){
                        for(View item : indexContentAdapter.cacheView){
                                LinearLayout menuLinearLayout = (LinearLayout)item.findViewById(R.id.item_menu);
                                LinearLayout hideLinearLayout = (LinearLayout)item.findViewById(R.id.hide_layout);
                                indexContentAdapter.closeMenu(menuLinearLayout, hideLinearLayout, 500);
                            }
                        indexContentAdapter.cacheView.clear();
                    }
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
            }
        });
    }
}





