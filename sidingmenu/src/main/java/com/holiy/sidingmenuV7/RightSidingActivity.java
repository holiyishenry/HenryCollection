package com.holiy.sidingmenuV7;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.holiy.R;

public class RightSidingActivity extends Activity {

    private BaseSidingMenuV7 mLeftMenu;
    private ListView listView;
    String[] values = new String[]{
            "Stop Animation (Back icon)",
            "Stop Animation (Home icon)",
            "Start Animation",
            "Change Color",
            "MYDialog",
            "Share",
            "Rate",
            "Stop Animation (Back icon)",
            "Stop Animation (Home icon)",
            "Start Animation",
            "Change Color",
            "MYDialog",
            "Share",
            "Rate",
            "Stop Animation (Back icon)",
            "Stop Animation (Home icon)",
            "Start Animation",
            "Change Color",
            "MYDialog",
            "Share",
            "Rate"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_right_siding);
        mLeftMenu = (BaseSidingMenuV7)findViewById(R.id.id_menu);
        listView = (ListView)findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        listView.setAdapter(adapter);
    }

    /**
     * 开关
     * @param view
     */
    public void toggleRightMenu(View view){
//        mLeftMenu.toggleRightMenu();
    }






}
