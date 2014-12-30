package com.henry.tablewidget.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.henry.tablewidget.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;


/**
 * 浮动圆球桌面默认信息，添加了菜单效果，出现fun是恶搞
 */
public class TableWidgetFloatView_fun extends RelativeLayout {

    private static final String TAG = "MainActivity";

    private Button mMenuButton;
    private Button mItemButton1;
    private Button mItemButton2;
    private Button mItemButton3;
    private Button mItemButton4;
    private Button mItemButton5;

    public int mMenuWidth;
    public int mMenuHeight;
    /**
     * 是否打开了菜单
     */
    private boolean mIsMenuOpen = false;
    /**
     * 是否显示了移除图标
     */
    private boolean mIsRemoveBtnOpen = false;
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
    /**
     * menu靠边操作线程辅助变量
     */
    private int temp;


    public TableWidgetFloatView_fun(Context context) {
        this(context, null);
    }

    public TableWidgetFloatView_fun(Context context, AttributeSet attrs) {
        super(context, attrs);
        getDisplayInfo(context);
        initView(context);
    }

    /**
     * 获取屏幕信息
     */
    private void getDisplayInfo(Context context){
        // Display类提供关于屏幕尺寸和分辨率的信息
        WindowManager winManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        screenWidth = winManager.getDefaultDisplay().getWidth();
        screenHeight = winManager.getDefaultDisplay().getHeight();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.window_float_menu_layout_fun, this);
        //---------------------以下为初始化菜单操作---------------------------
        mMenuButton = (Button) findViewById(R.id.menu);
        mMenuButton.setOnTouchListener(new MenuButtonOnTouchListener(mMenuButton));
        mMenuWidth = mMenuButton.getLayoutParams().width;
        mMenuHeight = mMenuButton.getLayoutParams().height;

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
        //根据菜单最终停留的位置，决定item打开方向
        int translationX = (int) (radius * Math.cos(degree));
        int translationY = (int) (radius * Math.sin(degree));
        //将屏幕分为四块，4种情况
        if(lastX >= screenWidth / 2 && lastY < screenHeight / 2){//右上角
            translationX = -translationX;
        }else if(lastX < screenWidth / 2 && lastY > screenHeight / 2){//左下角
            translationY = -translationY;
        }else if(lastX >= screenWidth / 2 && lastY > screenHeight / 2){//右下角
            translationX = -translationX;
            translationY = -translationY;
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
        translationX = (int) (radius * Math.cos(degree));
        translationY = (int) (radius * Math.sin(degree));
        //将屏幕分为四块，4种情况
        if(lastX >= screenWidth / 2 && lastY < screenHeight / 2){//右上角
            translationX = -translationX;
        }else if(lastX < screenWidth / 2 && lastY > screenHeight / 2){//左下角
            translationY = -translationY;
        }else if(lastX >= screenWidth / 2 && lastY > screenHeight / 2){//右下角
            translationX = -translationX;
            translationY = -translationY;
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
    private class MenuButtonOnTouchListener implements OnTouchListener {
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
                    if((Math.abs(lastX - startX) >= 5 || Math.abs(lastY - startY) >= 5)){
                        if(mIsMenuOpen){//关闭菜单
                            closeMenuItem();
                        }
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
                    }else{
                        //当移动结束，手指离开时判定是否将menu放置到侧边
//                        keepToSide(mMenuButton);
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

    /**
     * 将菜单放置到侧边
     * @param view

    private void keepToSide(final View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(view.getLeft() < screenWidth / 2 - view.getWidth() / 2){
                    for(int i = view.getLeft(); i > 0;i = i - 2){
                        temp = i;
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                view.layout(temp, view.getTop(),temp + view.getWidth(),  view.getTop() + view.getHeight());
                            }
                        });
                    }
                }else{
                    for(int i = view.getLeft(); i < screenWidth - view.getWidth(); i+= 2){
                        temp = i;
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                view.layout(temp, view.getTop(), temp + view.getWidth(), view.getTop() + view.getHeight());
                            }
                        });
                    }
                }
            }
        }).start();
    }
     */
}
