package com.holiy.sidingmenuV4;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.Window;

import com.holiy.R;
/**
 * QQ缩放侧滑效果
 * @author holiy
 *
 */
public class MainActivityV4 extends Activity {

    private BaseSidingMenuV4 mLeftMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main4);
        mLeftMenu = (BaseSidingMenuV4)findViewById(R.id.id_menu);
    }

    /**
     * 开关
     * @param view
     */
    public void toggleMenu(View view){
        mLeftMenu.toggle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
