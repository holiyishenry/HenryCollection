package com.holiy.sidingmenuV6;

import com.nineoldandroids.view.ViewHelper;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.holiy.R;
/**
 * 缩放侧滑效果,左右两边都能缩放,添加了阴影蒙版，添加教程步骤提示
 * @author holiy
 */
public class BaseSidingMenuV6 extends  HorizontalScrollView{

    private LinearLayout mWapper;
    private ViewGroup mLeftMenu;//左边菜单
    private ViewGroup mRightMenu;//右边菜单
    private ViewGroup mContent;//主页
    private int mScreenWidth;//屏幕宽度
    private int mMenuWidth;//菜单宽度
    private LinearLayout shade;//阴影蒙版
    private boolean once;
    //单位dp
    private int mMenuRightPadding = 50;//menu出现后在右侧保留content大小
    private int openItem = 1;//0:左边，1:内容，2:右边菜单

    /**
     * 当使用了自定义属性时，使用此构造方法， 大部分自定义的样式，3个参数的方法是用于引入自定义的属性
     * @param context
     * @param attrs
     * @param defStyle
     */
    public BaseSidingMenuV6(Context context, AttributeSet attrs, int defStyle) {
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


    public BaseSidingMenuV6(Context context) {
        this(context, null, -1);
    }


    /**
     * @param context
     * @param attrs
     */
    public BaseSidingMenuV6(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }


    /**
     * 决定内部view的宽和高，以及自己的宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(!once){
            mWapper = (LinearLayout) getChildAt(0);
            mLeftMenu = (ViewGroup) mWapper.getChildAt(0);
            mContent = (ViewGroup) mWapper.getChildAt(1);
            mRightMenu = (ViewGroup) mWapper.getChildAt(2);
            mMenuWidth = mLeftMenu.getLayoutParams().width = mRightMenu.getLayoutParams().width = mScreenWidth - mMenuRightPadding;
            mContent.getLayoutParams().width = mScreenWidth;
            once = true;
            shade = (LinearLayout)mContent.findViewById(R.id.content_shade);
            shade.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeMenu();
                }
            });
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
     * 滑动比较耗时，换的子线程使用移动效果
     */
    private void mSmoothScrollTo(final int x, final int y){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                BaseSidingMenuV6.this.smoothScrollTo(x, y);//隐藏菜单，显示内容
            }
        });
    }


    /**
     * 手指动作，点击，移动等效果
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP://手指离开
                int scrollX = getScrollX();//隐藏在左边的宽度，并不是显示的宽度,偏移量
//			Log.i("scrollX", "scrollX:" + scrollX);
//			Log.i("mScreenWidth","mScreenWidth:"  + mScreenWidth);
//			Log.i("mMenuWidth","mMenuWidth:"  + mMenuWidth);
//			Log.i("pp", "pp:" + (mScreenWidth + (int)(0.33*(float)mMenuWidth)));
                if(mScreenWidth + (int)(0.33*(float)mMenuWidth) >= scrollX && scrollX >= mMenuWidth / 2){
                    mSmoothScrollTo(mMenuWidth, 0);
                    shade.setVisibility(View.GONE);//设置阴影
                    openItem = 1;
                }else if(scrollX < mMenuWidth / 2){//显示左边菜单
//				this.smoothScrollTo(0, 0);//显示菜单
                    mSmoothScrollTo(0, 0);
                    openItem = 0;
                    shade.setVisibility(View.VISIBLE);
                }else if(scrollX >= mScreenWidth + (int)(0.33*(float)mMenuWidth)){//显示右边菜单
//				this.smoothScrollTo(mMenuWidth * 2, 0);//显示菜单
                    mSmoothScrollTo(mMenuWidth * 2, 0);
                    openItem = 2;
                    shade.setVisibility(View.VISIBLE);
                }
                return true;
            case MotionEvent.ACTION_DOWN:
                int scrollX1 = getScrollX();
                if(mMenuWidth + mMenuRightPadding >= scrollX1 && scrollX1 > mMenuWidth - mMenuRightPadding){
                    closeMenu();
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }

    private void openLeftMenu(){
        if(openItem == 0) return;
//		this.smoothScrollTo(0, 0);//显示菜单
        mSmoothScrollTo(0,0);
        openItem = 0;
        shade.setVisibility(View.VISIBLE);
    }

    private void closeMenu(){
        if(openItem == 1) return;
//		this.smoothScrollTo(mMenuWidth, 0);
        mSmoothScrollTo(mMenuWidth, 0);
        openItem = 1;
        shade.setVisibility(View.GONE);//取消内容阴影
    }

    private void openRightMenu(){
        if(openItem == 2) return;
//		this.smoothScrollTo(2 * mMenuWidth, 0);
        mSmoothScrollTo(mMenuWidth * 2, 0);
        openItem = 2;
        shade.setVisibility(View.VISIBLE);
    }

    /**
     * 切换左边菜单
     */
    public void toggleLeftMenu(){
        if(openItem == 1){
            openLeftMenu();
        }else if(openItem == 0){
            closeMenu();
        }
    }

    /**
     * 切换右边菜单
     */
    public void toggleRightMenu(){
        if(openItem == 1){
            openRightMenu();
        }else if(openItem == 2){
            closeMenu();
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
        float scale = l*1.0f/mMenuWidth;//1 ~ 0
        float rScale = 1 - (l-mMenuWidth)*1.0f/mMenuWidth;//1 ~ 0， 当右滑时l>mMenuWidth
        if(l <= mMenuWidth){//内容区和左滑菜单
            //缩放比例
            float rightScale = 0.8f + 0.2f*scale;
            float leftScale = 1.0f - scale*0.3f;
            //透明度
            float leftAlpha = 0.2f + 0.8f*(1-scale);

            ViewHelper.setTranslationX(mLeftMenu, mMenuWidth*scale*0.2f);//右边参数是初始隐藏大小
            //------------------菜单的效果-----------------
            ViewHelper.setScaleX(mLeftMenu, leftScale);//缩放
            ViewHelper.setScaleY(mLeftMenu, leftScale);
            ViewHelper.setAlpha(mLeftMenu, leftAlpha);//透明度
            //---------------------右侧内容效果--------------------
            //-------------设置content缩放的中心点为左侧中心
            ViewHelper.setPivotX(mContent, 0);
            ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
            //-------------设置content缩放的中心点
            ViewHelper.setScaleX(mContent, rightScale);//内容变化
            ViewHelper.setScaleY(mContent, rightScale);
        }else{//内容区和右滑菜单
            //缩放比例
            float leftScale = 0.8f + 0.2f*rScale;
            float rightScale = 1.0f - rScale*0.4f;
            //透明度
            float rightAlpha = 0.5f + 0.5f*(1-rScale);
            ViewHelper.setTranslationX(mRightMenu, mMenuWidth*rScale*0.2f);//右边参数是初始隐藏大小
//			Log.i("mMenuWidth*rScale*0.7f", "mMenuWidth*rScale*0.7f" + mMenuWidth*rScale*0.7f);
            //------------------菜单的效果-----------------
            ViewHelper.setScaleX(mRightMenu, rightScale);//缩放
            ViewHelper.setScaleY(mRightMenu, rightScale);
            ViewHelper.setAlpha(mRightMenu, rightAlpha);//透明度
            //-----------------设置中心点------------------------------
            ViewHelper.setPivotX(mContent, mScreenWidth);
            ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
            //---------------------左侧内容效果--------------------
            ViewHelper.setScaleX(mContent, leftScale);//内容变化
            ViewHelper.setScaleY(mContent, leftScale);
        }
    }
    //------------------------------------------------
    //菜单的显示时有缩放和透明度变化
    //缩放 0.7~1.0
    //1.0-scale*0.3
    //透明度：0.6~1.0
    //0.6+0.4*(1-scale)
    //------------------------------------------------
}
