package com.holiy.sidingmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
/**
 * 最原始的侧滑效果
 * @author holiy
 *
 */
public class BaseSidingMenu extends HorizontalScrollView {

    private LinearLayout mWapper;
    private ViewGroup mMenu;//菜单
    private ViewGroup mContent;//主页
    private int mScreenWidth;//屏幕宽度
    private int mMenuWidth;//菜单宽度
    private boolean once;
    //单位dp
    private int mMenuRightPadding = 50;//menu出现后在右侧保留content大小

    /**
     * 未使用自定义时调用
     * @param context
     * @param attrs
     */
    public BaseSidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;

        //把dp转换为px（单位间转换）
        mMenuRightPadding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
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
                }else{
                    this.smoothScrollTo(0, 0);//显示菜单
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }

}
