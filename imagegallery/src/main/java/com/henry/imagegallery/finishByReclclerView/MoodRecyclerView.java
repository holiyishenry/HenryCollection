package com.henry.imagegallery.finishByReclclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @author henry
 * 图集效果，同时能够监听到是中间ImageView对象，和前后对象，通过OnScrollListener回调
 * 来自由添加联动动画
 */
public class MoodRecyclerView extends RecyclerView implements OnScrollListener
{

	/**
	 * 记录中央的View
	 */
	private View mCurrentView;

    /**
     * 前一个view
     */
    private View mFrontView;
    /**
     * 后一个view
     */
    private View mNextView;

	private OnItemScrollChangeListener mItemScrollChangeListener;

	public void setOnItemScrollChangeListener(
			OnItemScrollChangeListener mItemScrollChangeListener)
	{
		this.mItemScrollChangeListener = mItemScrollChangeListener;
	}

	public interface OnItemScrollChangeListener
	{
		void onChange(View frontView, View middleView, View nextView, int position);
	}

	public MoodRecyclerView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.setOnScrollListener(this);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);
        int middle = getChildCount() / 2;
        if(middle == 0){
            middle = 1;
        }
        Log.i("middle", "middle:" + middle);
        mFrontView = getChildAt(middle - 1);
		mCurrentView = getChildAt(middle);
        mNextView = getChildAt(middle + 1);

		if (mItemScrollChangeListener != null)
		{
			mItemScrollChangeListener.onChange(mFrontView, mCurrentView, mNextView,
					getChildPosition(mCurrentView));
		}
	}


	@Override
	public void onScrollStateChanged(int arg0)
	{
	}

	/**
	 * 
	 * 滚动时，判断当前中央的View是否发生变化，发生才回调
	 */
	@Override
	public void onScrolled(int arg0, int arg1)
	{
        int middle = getChildCount() / 2;
        if(middle == 0){
            middle = 1;
        }
        View frontView = getChildAt(middle - 1);
		View middleView = getChildAt(middle);
        View nextView = getChildAt(middle + 1);
		if (mItemScrollChangeListener != null)
		{
			if (middleView != null && middleView != mCurrentView)
			{
                mFrontView = frontView;
				mCurrentView = middleView ;
                mNextView = nextView;
                mItemScrollChangeListener.onChange(mFrontView, mCurrentView, mNextView,
                        getChildPosition(mCurrentView));
			}
		}
	}
}
