package com.holiy.sidingmenuV7;

import com.nineoldandroids.view.ViewHelper;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.holiy.R;
/**
 * 右边侧滑出菜单效果
 * @author henry
 */
public class BaseSidingMenuV7 extends HorizontalScrollView {

    private LinearLayout mWapper;
    private ViewGroup mMenu;//菜单
    private ViewGroup mContent;//主页
    private int mScreenWidth;//屏幕宽度
    private int mMenuWidth;//菜单宽度
    private boolean once;
    //单位dp
    private int mMenuRightPadding = 50;//menu出现后在右侧保留content大小

    private boolean isOpen;//是否打开了菜单,用于按键的开关菜单

    private LinearLayout shade;//蒙版

    public boolean isMoveable = false;
    /**
     * 当使用了自定义属性时，使用此构造方法， 大部分自定义的样式，3个参数的方法是用于引入自定义的属性
     * @param context
     * @param attrs
     * @param defStyle
     */
    public BaseSidingMenuV7(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //获取我们定义的自定义属性
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SlidingMenu, defStyle, 0);
        int n = a.getIndexCount();
        a.recycle();
        for(int i = 0; i < n; i++){//当定义了多个自定义的属性时要每个遍历出来
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.SlidingMenu_rightPadding:
                    mMenuRightPadding = a.getDimensionPixelOffset(attr,  (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()));//后一个参数为默认50dp
                    break;
            }
        }

        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;

        //把dp转换为px（单位间转换）
//		mMenuRightPadding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
    }


    public BaseSidingMenuV7(Context context) {
        this(context, null, -1);
    }


    /**
     * @param context
     * @param attrs
     */
    public BaseSidingMenuV7(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }


    /**
     * 决定内部view的宽和高，以及自己的宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(!once){
            mWapper = (LinearLayout) getChildAt(0);
            mContent = (ViewGroup) mWapper.getChildAt(0);
            mMenu = (ViewGroup) mWapper.getChildAt(1);
            mMenuWidth = mMenu.getLayoutParams().width = mMenuRightPadding;
            mContent.getLayoutParams().width = mScreenWidth;
            shade = (LinearLayout)mContent.findViewById(R.id.content_shade);
            shade.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeMenu();
                }
            });
            once = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 决定子view 放置的位置,通过设置偏移量将menu隐藏
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed){
            mSmoothScrollTo(0, 0);
        }
    }

    /**
     * 滑动比较耗时，换的子线程使用移动效果
     */
    private void mSmoothScrollTo(final int x, final int y){
//        if (isMoveable){
//            new Handler().post(new Runnable() {
//                @Override
//                public void run() {
//                    BaseSidingMenuV7.this.smoothScrollTo(x, y);//隐藏菜单，显示内容
//                }
//            });
//        }
          this.smoothScrollTo(x, y);//隐藏菜单，显示内容
    }


    /**
     * 手指动作，点击，移动等效果
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP://手指离开
                int scrollX = getScrollX();//隐藏在左边的宽度，并不是显示的宽度
                if(scrollX < mMenuWidth / 2){
                    closeMenu();
                }else{
                    openMenu();
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 打开菜单
     */
    public void openMenu(){
//        if(isOpen) return ;
        mSmoothScrollTo(mMenuWidth, 0);//显示菜单
        shade.setVisibility(VISIBLE);
        isOpen = true;
    }

    /**
     * 关闭菜单
     */
    public void closeMenu(){
//        if(!isOpen) return ;
        mSmoothScrollTo(0, 0);//隐藏
        shade.setVisibility(GONE);
        isOpen = false;
    }

    /**
     * 切换菜单
     */
    public void toggle(){
        if(isOpen){
            closeMenu();
        }else{
            openMenu();
        }
    }

    /**
     * 监听滑动变化事件，滚动发生时
     * 左右滑动的判断，内容屏幕的中心点为初始位置，如手机屏幕是1120的，则初始l和oldl为560
     * 故要判断是否左滑出菜单或者右滑出，以560为基点，小于560为左滑，大于为右滑
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //调用属性动画，设置TranslationX;(Android 3.0开始使用的，为了兼容3.0以下版本，需要导入外部包，从github中下载得到，在lib中)
        float scale = l*1.0f/mMenuWidth;//1~0
        //让Content在菜单的左侧，默认情况下Menu在content之上，所以我们根据菜单划出的距离给Content设置X方向的偏移量。
        //为知笔记的“设置菜单弹出效果”
        ViewHelper.setTranslationX(mMenu, mMenuWidth *(1- scale));//右边参数是初始隐藏大小
        ViewHelper.setTranslationX(mContent, 0);

        ViewHelper.setScaleX(mMenu, scale* 0.5f + 0.5f);
        ViewHelper.setScaleY(mMenu, scale* 0.5f + 0.5f);
        ViewHelper.setAlpha(mMenu, scale);
    }
}
