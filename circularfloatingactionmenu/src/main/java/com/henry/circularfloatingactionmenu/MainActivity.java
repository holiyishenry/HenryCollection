package com.henry.circularfloatingactionmenu;

import com.nineoldandroids.animation.*;
import com.nineoldandroids.animation.Animator.AnimatorListener;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private Button mMenuButton;
    private Button mItemButton1;
    private Button mItemButton2;
    private Button mItemButton3;
    private Button mItemButton4;
    private Button mItemButton5;

    private boolean mIsMenuOpen = false;
    /**
     * 获取屏幕的宽高
     */
    private int screenWidth, screenHeight;// 屏幕宽高
    /**
     * 刚刚点击菜单时的坐标
     */
    private int startX, startY;
    /**
     * 菜单按钮最终定格坐标
     */
    private int lastX = 0,lastY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getDisplayInfo();
        initView();
    }

    /**
     * 获取屏幕信息
     */
    private void getDisplayInfo(){
        Display dis = this.getWindowManager().getDefaultDisplay();// Display类提供关于屏幕尺寸和分辨率的信息
        screenWidth = dis.getWidth();
        screenHeight = dis.getHeight();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initView() {
        mMenuButton = (Button) findViewById(R.id.menu);
        mMenuButton.setOnTouchListener(new MenuButtonOnTouchListener(mMenuButton));

        mItemButton1 = (Button) findViewById(R.id.item1);
//        mItemButton1.setOnClickListener(new MenuItemOnClickListener());

        mItemButton2 = (Button) findViewById(R.id.item2);
//        mItemButton2.setOnClickListener(new MenuItemOnClickListener());

        mItemButton3 = (Button) findViewById(R.id.item3);
//        mItemButton3.setOnClickListener(new MenuItemOnClickListener());

        mItemButton4 = (Button) findViewById(R.id.item4);
//        mItemButton4.setOnClickListener(new MenuItemOnClickListener());

        mItemButton5 = (Button) findViewById(R.id.item5);
//        mItemButton5.setOnClickListener(new MenuItemOnClickListener());
    }

    /**
     * 打开菜单的动画
     * @param view 执行动画的view
     * @param index view在动画序列中的顺序
     * @param total 动画序列的个数
     * @param radius 动画半径
     */
    private void doAnimateOpen(View view, int index, int total, int radius) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
        double degree = Math.PI * index / ((total - 1) * 2);
//        int translationX = (int) (radius * Math.cos(degree));
//        int translationY = (int) (radius * Math.sin(degree));
        int translationX, translationY;
        //根据菜单最终停留的位置，决定item打开方向
        if(lastX < screenWidth / 2){
            translationX = (int) (radius * Math.cos(degree));
            translationY = (int) (radius * Math.sin(degree));
        }else{
            translationX = (int) -(radius * Math.cos(degree));
            translationY = (int) (radius * Math.sin(degree));
        }
//        Log.i(TAG, String.format("degree=%f, translationX=%d, translationY=%d",
//                degree, translationX, translationY));
        AnimatorSet set = new AnimatorSet();
        //包含平移、缩放和透明度动画
        set.playTogether(
                ObjectAnimator.ofFloat(view, "translationX", mMenuButton.getLeft(),  mMenuButton.getLeft() + translationX),
                ObjectAnimator.ofFloat(view, "translationY", mMenuButton.getTop(), mMenuButton.getTop() + translationY),
                ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f),
                ObjectAnimator.ofFloat(view, "alpha", 0f, 1));
        //动画周期为500ms
        set.setDuration(1 * 500).start();
    }

    /**
     * 关闭菜单的动画,如果正在移动过程，则最终收缩的位置是可变的
     * @param view 执行动画的view
     * @param index view在动画序列中的顺序
     * @param total 动画序列的个数
     * @param radius 动画半径
     */
    private void doAnimateClose(final View view, int index, int total,
            int radius) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
        double degree = Math.PI * index / ((total - 1) * 2);
        int translationX, translationY;
        if(lastX < screenWidth / 2){
            translationX = (int) (radius * Math.cos(degree));
            translationY = (int) (radius * Math.sin(degree));
        }else{
            translationX = (int) -(radius * Math.cos(degree));
            translationY = (int) (radius * Math.sin(degree));
        }
//        Log.i(TAG, String.format("degree=%f, translationX=%d, translationY=%d",
//                degree, translationX, translationY));
        AnimatorSet set = new AnimatorSet();
      //包含平移、缩放和透明度动画
       set.playTogether(
                    ObjectAnimator.ofFloat(view, "translationX", translationX + mMenuButton.getLeft(), mMenuButton.getLeft()),
                    ObjectAnimator.ofFloat(view, "translationY", translationY + mMenuButton.getTop(), mMenuButton.getTop()),
                    ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f),
                    ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f),
                    ObjectAnimator.ofFloat(view, "alpha", 1f, 0f));
        //为动画加上事件监听，当动画结束的时候，我们把当前view隐藏
        set.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }
        });
        set.setDuration(1 * 500).start();
    }

    /**
     * 菜单按钮滑动事件
     */
    private class MenuButtonOnTouchListener implements View.OnTouchListener {
        private Button button;

        public MenuButtonOnTouchListener(Button mMenuButton) {
            this.button = mMenuButton;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN://手指按下
                    startX = lastX = (int) event.getRawX();
                    startY = lastY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE://手指移动
                    //如果打开了菜单，需要关闭菜单,有跟随的幻影效果
                    if((Math.abs(lastX - startX) >= 5 || Math.abs(lastY - startY) >= 5) && mIsMenuOpen){
                        closeMenuItem();
                    }

                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;

                    int top = v.getTop() + dy;
                    int left = v.getLeft() + dx;

                    if (top <= 0) {
                        top = 0;
                    }
                    if (top >= screenHeight - button.getHeight()) {
                        top = screenHeight - button.getHeight();
                    }
                    if (left >= screenWidth - button.getWidth()) {
                        left = screenWidth - button.getWidth();
                    }
                    if (left <= 0) {
                        left = 0;
                    }
                    v.layout(left, top, left + button.getWidth(),
                            top + button.getHeight());
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();

                    break;
                case MotionEvent.ACTION_UP://手指离开
                    if(Math.abs(lastX - startX)<= 5 && Math.abs(lastY - startY) <= 5){
                    //没有移动或者很小的移动都算点击事件
                        setItemAnim();
                    }
                    break;
            }
            return false;
        }
    }

    /**
     * MenuItem的开关
     */
    private void setItemAnim(){
       if (!mIsMenuOpen){
            openMenuItem();
       }else{
            closeMenuItem();
       }
    }

    /**
     * 打开菜单项
     */
    private void openMenuItem(){
         mIsMenuOpen = true;
         doAnimateOpen(mItemButton1, 0, 5, 250);
         doAnimateOpen(mItemButton2, 1, 5, 250);
         doAnimateOpen(mItemButton3, 2, 5, 250);
         doAnimateOpen(mItemButton4, 3, 5, 250);
         doAnimateOpen(mItemButton5, 4, 5, 250);
    }

    /**
     * 关闭菜单项
     */
    private void closeMenuItem(){
        mIsMenuOpen = false;
        doAnimateClose(mItemButton1, 0, 5, 250);
        doAnimateClose(mItemButton2, 1, 5, 250);
        doAnimateClose(mItemButton3, 2, 5, 250);
        doAnimateClose(mItemButton4, 3, 5, 250);
        doAnimateClose(mItemButton5, 4, 5, 250);
    }

}
