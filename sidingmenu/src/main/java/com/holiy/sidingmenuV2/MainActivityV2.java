package com.holiy.sidingmenuV2;

import com.holiy.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
/**
 * 普通侧滑的风格，有开关和滑动
 * @author holiy
 *
 */
public class MainActivityV2 extends Activity {

    private BaseSidingMenuV2 mLeftMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main2);
        mLeftMenu = (BaseSidingMenuV2)findViewById(R.id.id_menu);
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
