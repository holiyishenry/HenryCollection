package com.holiy.sidingmenuV6;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.holiy.R;
/**
 * 缩放侧滑效果，左右两边都缩放
 * @author holiy
 *
 */
public class MainActivityV6 extends Activity {

    private BaseSidingMenuV6 mLeftMenu;
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
        setContentView(R.layout.activity_main6);
        mLeftMenu = (BaseSidingMenuV6)findViewById(R.id.id_menu);
        listView = (ListView)findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        listView.setAdapter(adapter);

        startActivityForResult(
                new Intent(this,
                        GuideHintActivity.class), 2);
    }

    /**
     * 开关
     * @param view
     */
    public void toggleMenu(View view){
        mLeftMenu.toggleLeftMenu();
    }

    public void toggleRightMenu(View view){
        mLeftMenu.toggleRightMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == 2) {

        }
    }

}
