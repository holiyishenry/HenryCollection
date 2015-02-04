/*
 * Copyright 2014 Lars wufenglong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chuangxue.myapplication.widget.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import java.util.logging.MemoryHandler;

/**
 * 通过手势滑动控制Listview的滚动
 * write by wufenglong.
 */
public class HenryOldListView extends ListView{
    private final int START = 1;
    private final int END = 2;
    private final int ENDMOVE = 3;
    private int ITEM_HEIGHT;//标准item高,
    private int mITEM_MAX_HEIGHT = 0;
    private int mLastFirstVisiblePosition = 0;
    private Thread mThread;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case START:
                    changeItemHeightOnScroll();
                    break;
                case ENDMOVE:
                    endChangeItem();
                    break;
                case END:
                    if(mThread != null){
                        mThread = null;
                    }
                    break;
            }
        }
    };
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
            Log.i("status", "onDown");
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
//            endChangeItem();
            mHandler.sendEmptyMessage(ENDMOVE);
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //将值4舍5入到最接近的整数或指定的小数位数，上滑动时distanceY<0,下滑动时distanceY>0
            if(distanceY < 0){
                smoothScrollBy(Math.round(distanceY), 0);//滑动
            }else{
                smoothScrollBy(Math.round(distanceY * 3), 0);//滑动
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
                } else {
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
            mHandler.sendEmptyMessage(START);
//            changeItemHeightOnScroll();
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i("status", "onLongPress");
        }

        /**
         * 用户按下触摸屏、快速移动后松开，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE, 1个ACTION_UP触发
         * 是在onscroll方法结束后执行的
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.i("status", "onFling");
//            endChangeItem();
            mHandler.sendEmptyMessage(ENDMOVE);
            return false;
        }

    }

    /**
     * 模拟移动线程
     */
    private void moveThread(){
        if(mThread == null) {
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (distanceOneItem > 0) {
                        while (distanceOneItem > 0) {
                            distanceOneItem--;
                            Log.i("info", "moveThread distanceOneItem:" + distanceOneItem);
                            smoothScrollBy(1, 0);
                            if(distanceOneItem > 0){
                                mHandler.sendEmptyMessage(START);
                            }else{
                                mHandler.sendEmptyMessage(END);
                            }
                        }
                    } else if (distanceOneItem < 0) {
                        while (distanceOneItem < 0) {
                            distanceOneItem++;
                            Log.i("info", "moveThread distanceOneItem:" + distanceOneItem);
                            smoothScrollBy(-1, 0);
                            if(distanceOneItem < 0){
                                mHandler.sendEmptyMessage(START);
                            }else{
                                mHandler.sendEmptyMessage(END);
                            }
                        }
                    }
                }
            });
            mThread.start();
        }
    }




    public HenryOldListView(Context context) {
        super(context);
        init(context);
    }

    public HenryOldListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mGestureDetector = new GestureDetector(context, new MyGestureDetector());
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }


    /**
     * item的最终归位
     */
    private void endChangeItem(){
        //添加模拟滑动事件
        View firstView = getChildAt(0);
        if(firstView != null){
            if(firstView.getTop() < 0){
                if(firstView.getBottom() > firstView.getLayoutParams().height / 2){
//                    Log.i("info", "endChangeItem1");
                    //剩余的向下滑动
                    mItemSurplusHeight = firstView.getTop();
                    //将item0放大到最大，item1缩小到正常
                    changeItemHeightOnFling(mITEM_MAX_HEIGHT, ITEM_HEIGHT);
                    //参数为正数时，向上滑动，反之，向下滑动
                    smoothScrollBy(mItemSurplusHeight, 500);
                    distanceOneItem = 0;
                }else{
//                    Log.i("info", "endChangeItem2");
                    //将item0缩小到正常，item1放大到最大
                    changeItemHeightOnFling(mITEM_MAX_HEIGHT, mITEM_MAX_HEIGHT);
                    //剩余的向上滑动
                    mItemSurplusHeight  = firstView.getBottom();
                    //参数为正数时，向上滑动，反之，向下滑动
                    smoothScrollBy(mItemSurplusHeight, 500);
//                    distanceOneItem = 0;
                }
            }
        }
    }

    private void changeItemHeightOnFling(int changeHeight, int changeHeight1){
        View item0 = getChildAt(0);
        View item1 = getChildAt(1);
        if(item0 != null && item1 != null){
            item0.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, changeHeight));
            item1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, changeHeight1));
        }
    }

    private void changeItemHeightOnScroll() {
        View item0 = getChildAt(0);
        View item1 = getChildAt(1);

        int changeHeight1;
        int change;
        int changeHeight;
        if (distanceOneItem == 0) return;
        Log.i("info", "distanceOneItem:" + distanceOneItem);
        /**
         * 根据distanceOneItem的值调控item的高度
         * 1、当distanceOneItem > 0,且distanceOneItem不断增大时，item0缩小，item1放大（同一个item下滑）
         * ，distanceOneItem缩小时，item0放大，item1缩小
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
            change = changeHeight1 - item1.getHeight();
            changeHeight = item0.getHeight() - change;
            if (changeHeight > mITEM_MAX_HEIGHT) {
                changeHeight = mITEM_MAX_HEIGHT;
            }
            if (changeHeight <= ITEM_HEIGHT) {
                changeHeight = ITEM_HEIGHT;
            }
//            item0.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, changeHeight));
            item1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, changeHeight1));
        }
        else {
            changeHeight1 = (ITEM_HEIGHT + distanceOneItem) * mITEM_MAX_HEIGHT / ITEM_HEIGHT;//缩小
            if (changeHeight1 > mITEM_MAX_HEIGHT) {
                changeHeight1 = mITEM_MAX_HEIGHT;
            }
            if (changeHeight1 <= ITEM_HEIGHT) {
                changeHeight1 = ITEM_HEIGHT;
            }
            change = item1.getHeight() - changeHeight1;
            changeHeight = item0.getHeight() + change;//放大
            if (changeHeight > mITEM_MAX_HEIGHT) {
                changeHeight = mITEM_MAX_HEIGHT;
            }
            if (changeHeight <= ITEM_HEIGHT) {
                changeHeight = ITEM_HEIGHT;
            }
            if(changeHeight > item0.getLayoutParams().height){
                item0.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, changeHeight));
            }
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