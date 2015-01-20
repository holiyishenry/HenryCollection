package com.henry.refreshlistview.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.henry.refreshlistview.R;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 带header和footer的listview
 */
public class UpAndDownReFlashListView extends ListView implements OnScrollListener {
    View header;// 顶部布局文件；
    View footer;// 脚部布局文件

    int headerHeight;// 顶部布局文件的高度；
    int footerHeight;//  脚部布局文件的高度；

    int firstVisibleItem;// 当前第一个可见的item的位置；
    int scrollState;// listview 当前滚动状态；
    boolean isHeaderRemark;// 标记，当前是在listview最顶端摁下的；
    boolean isFooderRemark;// 标记，当前是在listview最下端提上的
    int startY;// 摁下时的Y值；

    int state;// 当前的状态；
    final int NONE = 0;// 正常状态；
    //---------------------------下拉的状态start-------------------------
    final int PULL = 1;// 提示下拉状态；
    final int RELESE = 2;// 提示释放状态；
    final int REFLASHING = 3;// 刷新状态；
    OnHeaderReflashListener mHeaderReflashListener;//刷新数据的接口
    //---------------------------下拉的状态end-------------------------

    //---------------------------上提的状态start-------------------------
    final int PUSH = 4;//提示提起状态
    final int PUSH_RELESE = 5;// 提示提起释放状态
    final int PUSH_REFLASHING = 6;// 提起刷新状态

    boolean islastVisibleItem;

    OnFooterReflashListener mFooterReflashListener;//刷新数据的接口

    //---------------------------上提的状态end-------------------------

    public UpAndDownReFlashListView(Context context) {
        super(context);
        initView(context);
    }

    public UpAndDownReFlashListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public UpAndDownReFlashListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    /**
     * 初始化界面，添加顶部布局文件到 listview
     *
     * @param context
     */
    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        header = inflater.inflate(R.layout.self_reflash_header_layout, null);
        footer = inflater.inflate(R.layout.self_reflash_footer_layout,null);
        measureView(header, footer);

        headerHeight = header.getMeasuredHeight();
        footerHeight = footer.getMeasuredHeight();

//		Log.i("tag", "headerHeight = " + headerHeight);
        topPadding(-headerHeight);
        footerTopPadding(-footerHeight);

        this.addHeaderView(header);
        this.addFooterView(footer);
        this.setOnScrollListener(this);
    }

    /**
     * 通知父布局，占用的宽，高；
     * @param header
     * @param footer
     */
    private void measureView(View header, View footer) {
        ViewGroup.LayoutParams p = header.getLayoutParams();
        ViewGroup.LayoutParams q = footer.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        if(q == null){
            q = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int headerWidth = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int footerWidth = ViewGroup.getChildMeasureSpec(0, 0, q.width);
        int headerHeight;
        int footerHeight;
        int tempHeight = p.height;
        if (tempHeight > 0) {
            headerHeight = MeasureSpec.makeMeasureSpec(tempHeight,
                    MeasureSpec.EXACTLY);
        } else {
            headerHeight = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }

        tempHeight = q.height;
        if(tempHeight > 0){
            footerHeight = MeasureSpec.makeMeasureSpec(tempHeight,
                    MeasureSpec.EXACTLY);
        }else{
            footerHeight = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }

        header.measure(headerWidth, headerHeight);
        footer.measure(footerWidth, footerHeight);
    }

    /**
     * 设置header 布局 上边距；
     * @param topPadding
     */
    private void topPadding(int topPadding) {
        header.setPadding(header.getPaddingLeft(), topPadding,
                header.getPaddingRight(), header.getPaddingBottom());
        header.invalidate();
    }

    /**
     * 初始时隐藏footer需要。
     * @param topPadding
     */
    private void footerTopPadding(int topPadding) {
        footer.setPadding(footer.getPaddingLeft(), topPadding,
                footer.getPaddingRight(), footer.getPaddingBottom());
        footer.invalidate();
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // TODO Auto-generated method stub
        this.firstVisibleItem = firstVisibleItem;
        if(view.getLastVisiblePosition() == (view.getCount() - 1)){//是否到达底部
            islastVisibleItem = true;
        }else{
            islastVisibleItem = false;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub
        this.scrollState = scrollState;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (firstVisibleItem == 0) {
                    isHeaderRemark = true;
                    startY = (int) ev.getY();
                }else if(islastVisibleItem){
                    isFooderRemark = true;
                    startY = (int) ev.getY();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                onHeaderMove(ev);
                onFooterMove(ev);
                break;
            case MotionEvent.ACTION_UP:
                if (state == RELESE) {
                    state = REFLASHING;
                    // 加载最新数据；
                    reflashHeaderViewByState();
                    if(mHeaderReflashListener != null) {
                        mHeaderReflashListener.onHeaderReflash();
                    }
                }else if(state == PUSH_RELESE){
                    state = PUSH_REFLASHING;
                    // 加载下一页数据；
                    reflashFooterViewByState();
                    if(mFooterReflashListener != null){
                        mFooterReflashListener.onFooterReflash();
                    }
                }else if (state == PULL) {
                    state = NONE;
                    isHeaderRemark = false;
                    reflashHeaderViewByState();
                }else if(state == PUSH){
                    state = NONE;
                    isFooderRemark = false;
                    reflashFooterViewByState();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 判断移动过程操作；
     * @param ev
     */
    private void onHeaderMove(MotionEvent ev) {
        if (!isHeaderRemark) {
            return;
        }
        int tempY = (int) ev.getY();
        int space = tempY - startY;
        int topPadding = space - headerHeight;
        switch (state) {
            case NONE:
                if (space > 0) {
                    state = PULL;
                    reflashHeaderViewByState();
                }
                break;
            case PULL:
                topPadding(topPadding);
                if (space > headerHeight + 30
                        && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    state = RELESE;
                    reflashHeaderViewByState();
                }
                break;
            case RELESE:
                topPadding(topPadding);
                if (space < headerHeight + 30) {
                    state = PULL;
                    reflashHeaderViewByState();
                } else if (space <= 0) {
                    state = NONE;
                    isHeaderRemark = false;
                    reflashHeaderViewByState();
                }
                break;
        }
    }

    /**
     * 判断移动过程操作；当拉动大于30时，认为要刷新
     * @param ev
     */
    private void onFooterMove(MotionEvent ev) {
        if (!isFooderRemark) {
            return;
        }
        int tempY = (int) ev.getY();
        int space = startY - tempY;
//        int topPadding = space - footerHeight;
        switch (state) {
            case NONE:
                if (space > 0) {
                    state = PUSH;
                    reflashFooterViewByState();
                }
                break;
            case PUSH:
                footerTopPadding(0);
                if (space > footerHeight + 30
                        && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    state = PUSH_RELESE;
                    reflashFooterViewByState();
                }
                break;
            case PUSH_RELESE:
                topPadding(0);
                if (space < footerHeight + 30) {
                    state = PUSH;
                    reflashFooterViewByState();
                } else if (space <= 0) {
                    state = NONE;
                    isFooderRemark = false;
                    reflashFooterViewByState();
                }
                break;
        }
    }


    /**
     * 根据当前状态，改变界面显示，针对header的
     * 和footer分开写的好处是，2处可以方便设置不同的效果
     */
    private void reflashHeaderViewByState() {
        TextView tip = (TextView) header.findViewById(R.id.tip);
        ImageView arrow = (ImageView) header.findViewById(R.id.arrow);
        ProgressBar progress = (ProgressBar) header.findViewById(R.id.progress);
        RotateAnimation anim = new RotateAnimation(0, 180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(500);
        anim.setFillAfter(true);
        RotateAnimation anim1 = new RotateAnimation(180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim1.setDuration(500);
        anim1.setFillAfter(true);
        switch (state) {
            case NONE:
                arrow.clearAnimation();
                topPadding(-headerHeight);
                break;

            case PULL:
                arrow.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                tip.setText("下拉可以刷新！");
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
                topPadding(50);
                arrow.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                tip.setText("正在刷新...");
                arrow.clearAnimation();
                break;
        }
    }

    private void reflashFooterViewByState() {
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
                footerTopPadding(-footerHeight);
                break;

            case PUSH:
                arrow.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                tip.setText("上提可以刷新！");
                arrow.clearAnimation();
                arrow.setAnimation(anim1);
                break;
            case PUSH_RELESE:
                arrow.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                tip.setText("松开可以刷新！");
                arrow.clearAnimation();
                arrow.setAnimation(anim);
                break;
            case PUSH_REFLASHING:
                footerTopPadding(0);
                arrow.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                tip.setText("正在刷新...");
                arrow.clearAnimation();
                break;
        }
    }

    /**
     * 刷新获取完数据；
     * @param isHeader 如果是header下拉完成则为true，否则为false
     */
    public void reflashComplete(boolean isHeader) {
        state = NONE;
        TextView lastupdatetime;
        if(isHeader){
            isHeaderRemark = false;
            lastupdatetime = (TextView) header
                    .findViewById(R.id.lastupdate_time);
            reflashHeaderViewByState();
        }else{
            isFooderRemark = false;
            lastupdatetime = (TextView) footer
                    .findViewById(R.id.lastupdate_time);
            reflashFooterViewByState();
            //当上拉footer完成后，发现header会跑出来，所以这里要关上，具体原因尚未查明
            topPadding(-headerHeight);
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = format.format(date);
        lastupdatetime.setText(time);
    }

    /**
     * 上啦翻页获取完列表
     */
    public void footerReflashComplete(){

    }

    /**
     * 设置下拉监听
     * @param headerReflashListener
     */
    public void setOnHeaderReflashListener(OnHeaderReflashListener headerReflashListener){
        this.mHeaderReflashListener = headerReflashListener;
    }

    /**
     * 设置上提监听
     * @param footerReflashListener
     */
    public void setOnFooterReflashListener(OnFooterReflashListener footerReflashListener){
        this.mFooterReflashListener = footerReflashListener;
    }

    /**
     * 下拉刷新数据接口
     * @author Administrator
     */
    public interface OnHeaderReflashListener{
        public void onHeaderReflash();
    }

    /**
     * 上拉刷新数据接口
     * @author Administrator
     */
    public interface OnFooterReflashListener{
        public void onFooterReflash();
    }
}
