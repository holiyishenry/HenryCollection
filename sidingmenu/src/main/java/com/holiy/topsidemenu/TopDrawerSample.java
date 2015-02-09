package com.holiy.topsidemenu;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.holiy.R;
import com.holiy.topsidemenu.widget.view.MenuDrawer;
import com.holiy.topsidemenu.widget.view.Position;

/**
 * Sample class illustrating how to add a menu drawer above the content area.
 */
public class TopDrawerSample extends Activity implements OnClickListener {

    private static final int MENU_OVERFLOW = 1;

    private MenuDrawer mMenuDrawer;
    private TextView mContentTextView, menuTextView;

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        mMenuDrawer = MenuDrawer.attach(this, Position.TOP);
        mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
        mMenuDrawer.setContentView(R.layout.activity_topmenu);
        mMenuDrawer.setMenuView(R.layout.menu_top);
        mMenuDrawer.setMenuSize(1000);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//            getActionBar().setDisplayHomeAsUpEnabled(true);
//        }

        mContentTextView = (TextView) findViewById(R.id.contentText);
        menuTextView = (TextView)findViewById(R.id.menu_text);
        menuTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuDrawer.toggleMenu();
            }
        });
        findViewById(R.id.item1).setOnClickListener(this);
        findViewById(R.id.item2).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem overflowItem = menu.add(0, MENU_OVERFLOW, 0, null);
        MenuItemCompat.setShowAsAction(overflowItem, MenuItem.SHOW_AS_ACTION_ALWAYS);

        overflowItem.setIcon(R.drawable.ic_menu_moreoverflow_normal_holo_light);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case MENU_OVERFLOW:
                mMenuDrawer.toggleMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Click handler for top drawer items.
     */
    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        mContentTextView.setText(String.format("%s clicked.", tag));
        mMenuDrawer.setActiveView(v);
    }
}