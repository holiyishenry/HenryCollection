package com.henry.refreshlistview.widget.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView;
import com.henry.refreshlistview.R;

/**
 * 带footer的listview
 */
public class DownReflashListView extends ListView implements OnScrollListener {
    View footer;//底部加载样式
    int footerHeight;//底部高度

    int state;// 当前的状态；
    /**
     * 无操作
     */
    final int NONE = 0;// 正常状态；
    /**
     * 上提
     */
    final int PULL = 1;// 提示上拉状态；
    /**
     * 释放
     */
    final int RELESE = 2;// 提示释放状态；
    /**
     * 正在刷新
     */
    final int REFLASHING = 3;// 刷新状态；

    boolean islastVisibleItem;

    int scrollState;// listview 当前滚动状态；
    boolean isRemark;// 标记，当前是在listview最顶端摁下的；
    int startY;// 摁下时的Y值；

    FooterReflashListener footerReflashListener;//刷新数据的接口


    //第三个参数为自己添加的属性
    public DownReflashListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public DownReflashListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DownReflashListView(Context context) {
        super(context);
        initView(context);
    }


    protected void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        footer = inflater.inflate(R.layout.self_reflash_footer_layout, null);
        measureView(footer);
        footerHeight = footer.getMeasuredHeight();
//		Log.i("tag", "headerHeight = " + headerHeight);
        topPadding(-footerHeight);
        this.addFooterView(footer);
        this.setOnScrollListener(this);
    }

    /**
     * 通知父布局，占用的宽，高；
     *
     * @param view
     */
    private void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int height;
        int tempHeight = p.height;
        if (tempHeight > 0) {
            height = MeasureSpec.makeMeasureSpec(tempHeight,
                    MeasureSpec.EXACTLY);
        } else {
            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);
    }

    /**
     * 设置header 布局 上边距；初始时隐藏footer需要。
     * @param topPadding
     */
    private void topPadding(int topPadding) {
        footer.setPadding(footer.getPaddingLeft(), topPadding,
                footer.getPaddingRight(), footer.getPaddingBottom());
        footer.invalidate();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if(view.getLastVisiblePosition() == (view.getCount() - 1)){//是否到达底部
            islastVisibleItem = true;
        }else{
            islastVisibleItem = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (islastVisibleItem) {//如果是在最后
                    isRemark = true;
                    startY = (int) ev.getY();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                onMove(ev);
                break;
            case MotionEvent.ACTION_UP:
                if (state == RELESE) {
                    state = REFLASHING;
                    // 加载最新数据；
                    reflashViewByState();
                    footerReflashListener.onReflash();
                } else if (state == PULL) {
                    state = NONE;
                    isRemark = false;
                    reflashViewByState();
                }
                break;
        }

        return super.onTouchEvent(ev);
    }

    /**
     * 判断移动过程操作；当拉动大于30时，认为要刷新
     * @param ev
     */
    private void onMove(MotionEvent ev) {
        if (!isRemark) {
            return;
        }
        int tempY = (int) ev.getY();
        int space = startY - tempY;
        int topPadding = space - footerHeight;
        switch (state) {
            case NONE:
                if (space > 0) {
                    state = PULL;
                    reflashViewByState();
                }
                break;
            case PULL:
                topPadding(0);
                if (space > footerHeight + 30
                        && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    state = RELESE;
                    reflashViewByState();
                }
                break;
            case RELESE:
                topPadding(0);
                if (space < footerHeight + 30) {
                    state = PULL;
                    reflashViewByState();
                } else if (space <= 0) {
                    state = NONE;
                    isRemark = false;
                    reflashViewByState();
                }
                break;
        }
    }


    private void reflashViewByState() {
        TextView tip = (TextView)footer.findViewById(R.id.tip);
        ImageView arrow = (ImageView)footer.findViewById(R.id.arrow);
        ProgressBar progress = (ProgressBar) footer.findViewById(R.id.progress);

        RotateAnimation anim = new RotateAnimation(0, 180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(700);
        anim.setFillAfter(true);
        RotateAnimation anim1 = new RotateAnimation(180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim1.setDuration(700);
        anim1.setFillAfter(true);

        switch (state) {
            case NONE:
                arrow.clearAnimation();
                topPadding(-footerHeight);
                break;

            case PULL:
                arrow.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                tip.setText("上提可以刷新！");
                arrow.clearAnimation();
                arrow.setAnimation(anim1);
                break;
            case RELESE:
                arrow.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                tip.setText("松开可以刷新！");
                arrow.clearAnimation();
                arrow.setAnimation(anim);
                break;
            case REFLASHING:
                topPadding(0);
                arrow.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                tip.setText("正在刷新...");
                arrow.clearAnimation();
                break;
        }
    }

    /**
     * 获取完数据；
     */
    public void reflashComplete() {
        state = NONE;
        isRemark = false;
        reflashViewByState();
        TextView lastupdatetime = (TextView) footer
                .findViewById(R.id.lastupdate_time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = format.format(date);
        lastupdatetime.setText(time);
    }

    public void setInterface(FooterReflashListener footerReflashListener){
        this.footerReflashListener = footerReflashListener;
    }

    /**
     * 刷新数据接口
     * @author Administrator
     */
    public interface FooterReflashListener{
        public void onReflash();
    }

}
