package com.chuangxue.myapplication.widget.view.henrylistview;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.chuangxue.myapplication.R;

/**
 * @author henry
 * 带下拉刷新与上提翻页，第一个item放大的listView
 */
public class HenryListView extends ListView implements OnScrollListener {

	private float mLastY = -1; // save event y
	private Scroller mScroller; // used for scroll back
	private OnScrollListener mScrollListener; // user's scroll listener

	// the interface to trigger refresh and load more.
	private HenryListViewListener mListViewListener;

	// -- header view
	private HenryListViewHeader mHeaderView;
	private RelativeLayout mHeaderViewContent;
	private TextView mHeaderTimeView;
	private int mHeaderViewHeight; // header view's height
	private boolean mEnablePullRefresh = true;
	private boolean mPullRefreshing = false; // is refreashing.

	private HenryListViewFooter mFooterView;
	private boolean mEnablePullLoad;
	private boolean mPullLoading;
	private boolean mIsFooterReady = false;
	
	// total list items, used to detect is at the bottom of listview.
	private int mTotalItemCount;

	// for mScroller, scroll back from header or footer.
	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLLBACK_FOOTER = 1;

	private final static int SCROLL_DURATION = 400; // scroll back duration
	private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
														// at bottom, trigger
														// load more.
	private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
													// feature.

    /**
	 * @param context
	 */
	public HenryListView(Context context) {
		super(context);
		initWithContext(context);
	}

	public HenryListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public HenryListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	private void initWithContext(Context context) {
		mScroller = new Scroller(context, new DecelerateInterpolator());
		// XListView need the scroll event, and it will dispatch the event to
		// user's listener (as a proxy).
		super.setOnScrollListener(this);

		// init header view
		mHeaderView = new HenryListViewHeader(context);
		mHeaderViewContent = (RelativeLayout) mHeaderView
				.findViewById(R.id.xlistview_header_content);
		mHeaderTimeView = (TextView) mHeaderView
				.findViewById(R.id.xlistview_header_time);
		addHeaderView(mHeaderView);

		// init footer view
		mFooterView = new HenryListViewFooter(context);

		// init header height
		mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						mHeaderViewHeight = mHeaderViewContent.getHeight();
						getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});

        //first item max init
        mGestureDetector = new GestureDetector(context, new MyGestureDetector());
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){//手指抬起动作
                    postEndChangeItem();//防止item没有靠边
                }
                return mGestureDetector.onTouchEvent(event);
            }
        });
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		// make sure XListViewFooter is the last footer view, and only add once.
		if (mIsFooterReady == false) {
			mIsFooterReady = true;
			addFooterView(mFooterView);
		}
		super.setAdapter(adapter);
	}

	/**
	 * enable or disable pull down refresh feature.
	 * @param enable
	 */
	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh) { // disable, hide the content
			mHeaderViewContent.setVisibility(View.INVISIBLE);
		} else {
			mHeaderViewContent.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * enable or disable pull up load more feature.
	 * @param enable
	 */
	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			mFooterView.hide();
			mFooterView.setOnClickListener(null);
			//make sure "pull up" don't show a line in bottom when listview with one page 
			setFooterDividersEnabled(false);
		} else {
			mPullLoading = false;
			mFooterView.show();
			mFooterView.setState(HenryListViewFooter.STATE_NORMAL);
			//make sure "pull up" don't show a line in bottom when listview with one page  
			setFooterDividersEnabled(true);
			// both "pull up" and "click" will invoke load more.
			mFooterView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}

	/**
	 * stop refresh, reset header view.
	 */
	public void stopRefresh() {
		if (mPullRefreshing == true) {
			mPullRefreshing = false;
			resetHeaderHeight();
		}
	}

	/**
	 * stop load more, reset footer view.
	 */
	public void stopLoadMore() {
		if (mPullLoading == true) {
			mPullLoading = false;
			mFooterView.setState(HenryListViewFooter.STATE_NORMAL);
		}
	}

	/**
	 * set last refresh time
	 * @param time
	 */
	public void setRefreshTime(String time) {
		mHeaderTimeView.setText(time);
	}

	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}

	private void updateHeaderHeight(float delta) {
		mHeaderView.setVisiableHeight((int) delta
				+ mHeaderView.getVisiableHeight());
		if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
			if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
				mHeaderView.setState(HenryListViewHeader.STATE_READY);
			} else {
				mHeaderView.setState(HenryListViewHeader.STATE_NORMAL);
			}
		}
		setSelection(0); // scroll to top each time
	}

	/**
	 * reset header view's height.
	 */
	private void resetHeaderHeight() {
		int height = mHeaderView.getVisiableHeight();
		if (height == 0) // not visible.
			return;
		// refreshing and header isn't shown fully. do nothing.
		if (mPullRefreshing && height <= mHeaderViewHeight) {
			return;
		}
		int finalHeight = 0; // default: scroll back to dismiss header.
		// is refreshing, just scroll back to show all the header.
		if (mPullRefreshing && height > mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height,
				SCROLL_DURATION);
		// trigger computeScroll
		invalidate();
	}

	private void updateFooterHeight(float delta) {
		int height = mFooterView.getBottomMargin() + (int) delta;
		if (mEnablePullLoad && !mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load
													// more.
				mFooterView.setState(HenryListViewFooter.STATE_READY);
			} else {
				mFooterView.setState(HenryListViewFooter.STATE_NORMAL);
			}
		}
		mFooterView.setBottomMargin(height);

//		setSelection(mTotalItemCount - 1); // scroll to bottom
	}

	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
					SCROLL_DURATION);
			invalidate();
		}
	}

	private void startLoadMore() {
		mPullLoading = true;
		mFooterView.setState(HenryListViewFooter.STATE_LOADING);
		if (mListViewListener != null) {
			mListViewListener.onLoadMore();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();
			if (getFirstVisiblePosition() == 0
					&& (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
				// the first item is showing, header has shown or pull down.
				updateHeaderHeight(deltaY / OFFSET_RADIO);
				invokeOnScrolling();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1
					&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
				// last item, already pulled up or want to pull up.
				updateFooterHeight(-deltaY / OFFSET_RADIO);
			}
			break;
		default:
			mLastY = -1; // reset
			if (getFirstVisiblePosition() == 0) {
				// invoke refresh
				if (mEnablePullRefresh
						&& mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
					mPullRefreshing = true;
					mHeaderView.setState(HenryListViewHeader.STATE_REFRESHING);
					if (mListViewListener != null) {
						mListViewListener.onRefresh();
					}
				}
				resetHeaderHeight();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1) {
				// invoke load more.
				if (mEnablePullLoad
				    && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA
				    && !mPullLoading) {
					startLoadMore();
				}
				resetFooterHeight();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				mHeaderView.setVisiableHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
        // send to user's listener
		mTotalItemCount = totalItemCount;
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

	public void setXListViewListener(HenryListViewListener l) {
		mListViewListener = l;
	}

	/**
	 * you can listen ListView.OnScrollListener or this one. it will invoke
	 * onXScrolling when header/footer scroll back.
	 */
	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	/**
	 * implements this interface to get refresh/load more event.
	 */
	public interface HenryListViewListener {
		public void onRefresh();

		public void onLoadMore();
	}

    //------------------------------------first item max init-----------------------------------------
    private final int START = 1;
    private final int END = 2;
    private final int ENDMOVE = 3;
    private int ITEM_HEIGHT;//标准item高,
    private int mITEM_MAX_HEIGHT = 0;
    private int mLastFirstVisiblePosition = 0;
    /**
     * 互斥锁
     */
    private boolean lock = false;
    /**
     * 手势是否为上滑，false则为下滑
     */
    private boolean isUpMove = false;
    /**
     * 停止滚动后，item与顶端的距离
     */
    private int mItemSurplusHeight;
    /**
     * 记录滚动距离，向上滚动时-itemHeight到0，向下滚动是0到itemHeight,当listview FirstVisiblePosition改变时设置为0
     * 由于有缩放效果，所以itemHeight ！= ITEM_HEIGHT ！= mITEM_MAX_HEIGHT
     */
    private int distanceOneItem;
    private int mLastDistanceOneItem = 1;
    /**
     * android 手势判断类
     */
    private GestureDetector mGestureDetector;

    private class MyGestureDetector implements GestureDetector.OnGestureListener{
        // 用户轻触触摸屏，由1个MotionEvent ACTION_DOWN触发
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }
        /**
         * 用户轻触触摸屏，尚未松开或拖动，由一个1个MotionEvent ACTION_DOWN触发
         * 注意和onDown()的区别，强调的是没有松开或者拖动的状态
         */
        @Override
        public void onShowPress(MotionEvent e) {
            Log.i("status", "onShowPress");
        }
        // 用户（轻触触摸屏后）松开，由一个1个MotionEvent ACTION_UP触发,只有点击后放开才会执行，点击滑动再放开是不执行的
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.i("status", "onSingleTapUp");
//            postEndChangeItem();
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //将值4舍5入到最接近的整数或指定的小数位数，上滑动时distanceY<0,下滑动时distanceY>0
            if(distanceY < 0){
                smoothScrollBy(Math.round(distanceY), 0);//滑动
                isUpMove = true;
            }else{
                smoothScrollBy(Math.round(distanceY * 2), 0);//滑动
                isUpMove = false;
            }
            if (canScrollVertically(Math.round(distanceY))) {
                distanceOneItem += Math.round(distanceY);//同一个item滑动的距离，当item切换时需要清0
            } else {
                distanceOneItem = 0;//当滑动到顶部不能再下拉或者滑动到底部不能再上拉
                if (distanceY > 0) {
                    mLastDistanceOneItem = -1;
                } else {
                    mLastDistanceOneItem = 1;
                }
            }
            if (getFirstVisiblePosition() == mLastFirstVisiblePosition) {
                //从正变负或从负变正，但是firstposition没变
                if ((distanceY < 0 && (mLastDistanceOneItem >= 0 && distanceOneItem < 0))
                        || (distanceY > 0 && (mLastDistanceOneItem < 0 && distanceOneItem >= 0))) {
                    return false;
                } else if(((mLastDistanceOneItem != 1 || mLastDistanceOneItem != -1) && distanceY == distanceOneItem)
                        ||  (isUpMove && (mLastDistanceOneItem != 1 || mLastDistanceOneItem != -1) && mLastDistanceOneItem < distanceOneItem)){
                    return false;
                } else{
                    mLastDistanceOneItem = distanceOneItem;
                }
                mLastFirstVisiblePosition = getFirstVisiblePosition();
            } else {//当item切换时触发，如由item0——>itme1，反之亦然，distanceOneItem要清0
                mLastFirstVisiblePosition = getFirstVisiblePosition();
                distanceOneItem = 0;
                if (distanceY > 0) {
                    mLastDistanceOneItem = 1;
                } else {
                    mLastDistanceOneItem = -1;
                }
            }
//            postChangeItemHeightOnScroll();
            changeItemHeightOnScroll();
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        /**
         * 用户按下触摸屏、快速移动后松开，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE, 1个ACTION_UP触发
         * 是在onscroll方法结束后执行的
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            Log.i("status", "onFling");
//            postEndChangeItem();
            return false;
        }
    }


    private void postEndChangeItem(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                endChangeItem();
            }
        });
    }

    /**
     * item的最终归位
     * 注意：smoothScrollBy方法是异步执行的，后续操作不会等待smoothScrollBy持续时间后才执行而是立刻执行
     */
    private void endChangeItem(){
        if(lock){
           return;
        }
        lock = true;
        //添加模拟滑动事件
        int i = 0;
        View firstView = getChildAt(i);
        if(firstView.hashCode() == mHeaderView.hashCode()){
            firstView = getChildAt(++i);
        }
        if(firstView != null){
            if(firstView.getTop() < 0){
                if(firstView.getBottom() >= firstView.getLayoutParams().height / 2){ //剩余的向下滑动
                    changeItemHeightOnFling(mITEM_MAX_HEIGHT, ITEM_HEIGHT);
                    if(!isUpMove){//下滑动过程
                        //Log.i("status", "下滑过程向下收");
                        mItemSurplusHeight = firstView.getTop() + 5;
                        //将item0放大到最大，item1缩小到正常
                        smoothScrollBy(mItemSurplusHeight,  1200);
                    }else{//上滑动过程
//                        Log.i("status", "上滑过程向下收");
                        mItemSurplusHeight = firstView.getTop();
                        //将item0放大到最大，item1缩小到正常
                        smoothScrollBy(mItemSurplusHeight,  1200);
                    }
                }else{//余下的向上滑动
                    if(!isUpMove){//下滑动过程
//                        Log.i("status", "下滑过程向上收");
                        mItemSurplusHeight  = firstView.getBottom() + 5;
                        //参数为正数时，向上滑动，反之，向下滑动, mItemSurplusHeight + 10是为了确保item0一定是最大那个item
                        smoothScrollBy(mItemSurplusHeight,  1200);
                        //将item0缩小到正常，item1放大到最大
                        changeItemHeightOnFling(0, mITEM_MAX_HEIGHT);
                    }else{//上滑动过程
                        mItemSurplusHeight  =  firstView.getBottom() + 5;
                        //参数为正数时，向上滑动，反之，向下滑动, mItemSurplusHeight + 10是为了确保item0一定是最大那个item
                        smoothScrollBy(mItemSurplusHeight,  1000);
                        changeItemHeightOnFling(0, mITEM_MAX_HEIGHT);
//                        Log.i("status", "上滑过程向上收");
                    }
                }
                //清0操作
                distanceOneItem = 0;
                mLastFirstVisiblePosition = getFirstVisiblePosition();
            }
        }
        lock = false;
    }

    private void changeItemHeightOnFling(int changeHeight, int changeHeight1){
        int i = 0;
        View item0 = getChildAt(i);
        if(item0.hashCode() == mHeaderView.hashCode()){
            item0 = getChildAt(++i);
        }
        View item1 = getChildAt(i + 1);
        if(item0 != null && changeHeight > 0){
            item0.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, changeHeight));
        }
        if(item1 != null && changeHeight1 > 0){
            item1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, changeHeight1));
        }
    }

    private void postChangeItemHeightOnScroll(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                changeItemHeightOnScroll();
            }
        });
    }

    private void changeItemHeightOnScroll() {
        int i = 0;
        View item0 = getChildAt(i);
        if(item0.hashCode() == mHeaderView.hashCode()){
            item0 = getChildAt(++i);
        }
        View item1 = getChildAt(i + 1);
        int changeHeight1;
        int change;
        int changeHeight;
        if (distanceOneItem == 0) return;
        /**
         * 根据distanceOneItem的值调控item的高度
         * 1、当distanceOneItem > 0,且distanceOneItem不断增大时，item1放大（同一个item下滑）
         * ，distanceOneItem缩小时，item1缩小
         *
         * 2、当distanceOneItem < 0,且distanceOneItem不断减小，item0放大，itme1缩小（同一item上滑），
         * distanceOneItem增大时，itme0缩小，item1放大
         */
        if (distanceOneItem > 0) {
            changeHeight1 = distanceOneItem * mITEM_MAX_HEIGHT / ITEM_HEIGHT;//放大

            if (changeHeight1 > mITEM_MAX_HEIGHT) {
                changeHeight1 = mITEM_MAX_HEIGHT;
            }
            if (changeHeight1 <= ITEM_HEIGHT) {
                changeHeight1 = ITEM_HEIGHT;
            }
            item1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, changeHeight1));
        }
        else {
//            Log.i("info", "distanceOneItem:" + distanceOneItem);
//            Log.i("info", "mLastDistanceOneItem:" + mLastDistanceOneItem);
            changeHeight1 = (ITEM_HEIGHT + distanceOneItem) * mITEM_MAX_HEIGHT / ITEM_HEIGHT;//缩小
            if (changeHeight1 > mITEM_MAX_HEIGHT) {
                changeHeight1 = mITEM_MAX_HEIGHT;
            }
            if (changeHeight1 <= ITEM_HEIGHT) {
                changeHeight1 = ITEM_HEIGHT;
            }
            change = item1.getHeight() - changeHeight1;
            changeHeight = item0.getLayoutParams().height + change;//放大
            if (changeHeight > mITEM_MAX_HEIGHT) {
                changeHeight = mITEM_MAX_HEIGHT;
            }
            if (changeHeight <= ITEM_HEIGHT) {
                changeHeight = ITEM_HEIGHT;
            }
            //item0从隐藏后高度是adapter中item的默认高度,即ITEM_HEIGHT，所以出现时需要不断放大
            item0.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, changeHeight));
            item1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, changeHeight1));
        }
    }

    public int getItemHeight() {
        return ITEM_HEIGHT;
    }

    public void setItemHeight(int itemHeight) {
        this.ITEM_HEIGHT = itemHeight;
    }

    public int getItemMaxHeight() {
        return mITEM_MAX_HEIGHT;
    }

    public void setItemMaxHeight(int itemMaxHeight) {
        this.mITEM_MAX_HEIGHT = itemMaxHeight;
    }

    /**
     * Check if this view can be scrolled vertically in a certain direction.
     * @param direction Negative to check scrolling up, positive to check scrolling down.
     * @return true if this view can be scrolled in the specified direction, false otherwise.
     */
    public boolean canScrollVertically(int direction) {
        final int offset = computeVerticalScrollOffset();
        final int range = computeVerticalScrollRange() - computeVerticalScrollExtent();
        if (range == 0) return false;
        if (direction < 0) {
            return offset > 0;
        } else {
            return offset < range - 1;
        }
    }
}
