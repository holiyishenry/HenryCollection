package com.holiy.sidingmenuV3;


import com.holiy.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
/**
 * 抽屉式侧滑菜单，感觉底下的菜单不动，内容在动
 * @author holiy
 *
 */
public class MainActivityV3 extends Activity {

    private BaseSidingMenuV3 mLeftMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main3);
        mLeftMenu = (BaseSidingMenuV3)findViewById(R.id.id_menu);
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
