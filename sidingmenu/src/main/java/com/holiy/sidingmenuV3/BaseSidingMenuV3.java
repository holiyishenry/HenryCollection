package com.holiy.sidingmenuV3;

import java.lang.reflect.TypeVariable;

import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.content.res.TypedArray;
import android.hardware.display.DisplayManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.holiy.R;

public class BaseSidingMenuV3 extends HorizontalScrollView {

    private LinearLayout mWapper;
    private ViewGroup mMenu;//菜单
    private ViewGroup mContent;//主页
    private int mScreenWidth;//屏幕宽度
    private int mMenuWidth;//菜单宽度
    private boolean once;
    //单位dp
    private int mMenuRightPadding = 50;//menu出现后在右侧保留content大小

    private boolean isOpen;//是否打开了菜单,用于按键的开关菜单

    /**
     * 当使用了自定义属性时，使用此构造方法， 大部分自定义的样式，3个参数的方法是用于引入自定义的属性
     * @param context
     * @param attrs
     * @param defStyle
     */
    public BaseSidingMenuV3(Context context, AttributeSet attrs, int defStyle) {
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


    public BaseSidingMenuV3(Context context) {
        this(context, null, -1);
    }


    /**
     * @param context
     * @param attrs
     */
    public BaseSidingMenuV3(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }


    /**
     * 决定内部view的宽和高，以及自己的宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(!once){
            mWapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) mWapper.getChildAt(0);
            mContent = (ViewGroup) mWapper.getChildAt(1);
            mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuRightPadding;
            mContent.getLayoutParams().width = mScreenWidth;
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
            this.scrollTo(mMenuWidth, 0);
        }
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
                if(scrollX >= mMenuWidth / 2){
                    this.smoothScrollTo(mMenuWidth, 0);//隐藏
                    isOpen = false;
                }else{
                    this.smoothScrollTo(0, 0);//显示菜单
                    isOpen = true;
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 打开菜单
     */
    public void openMenu(){
        if(isOpen) return ;
        this.smoothScrollTo(0, 0);
        isOpen = true;
    }

    /**
     * 关闭菜单
     */
    public void closeMenu(){
        if(!isOpen) return ;
        this.smoothScrollTo(mMenuWidth, 0);
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

    //------------------------------------------------
    /**
     * 监听滑动变化事件，滚动发生时
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //调用属性动画，设置TranslationX;(Android 3.0开始使用的，为了兼容3.0以下版本，需要导入外部包，从github中下载得到，在lib中)
        float scale = l*1.0f/mMenuWidth;//1~0
        ViewHelper.setTranslationX(mMenu, mMenuWidth*scale);
    }
}
