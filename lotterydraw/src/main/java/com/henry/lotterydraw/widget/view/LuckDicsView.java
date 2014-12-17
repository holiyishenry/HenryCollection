package com.henry.lotterydraw.widget.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.henry.lotterydraw.R;


/**
 * 使用SurfaceView 实现幸运抽奖转盘
 * @author henry
 */
public class LuckDicsView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    /**
     * 用于操作surface
     */
    private SurfaceHolder mHolder;
    /**
     * 画布
     */
    private Canvas mCanvas;

    private Thread mThread;
    /**
     * 线程运行状态
     */
    private boolean isRunning;

    /**
     * 盘块的个数
     */
    private int mItemCount;

    /**
     * 抽奖的物品
     */
    private String[] award;
    /**
     * 每个盘块的颜色
     */
    private int[] mColors;

    /**
     * 与文字对应的图片
     */
    private int[] awardImgs;
    /**
     * 与文字对应图片的bitmap数组
     */
    private Bitmap[] mImgsBitmap;
    /**
     * 绘制盘块的范围
     */
    private RectF mRange = new RectF();

    /**
     * 圆的直径
     */
    private int mRadius;

    /**
     * 绘制盘块的画笔
     */
    private Paint mArcPaint;

    /**
     * 绘制文字的画笔
     */
    private Paint mTextPaint;

    /**
     * 滚动的速度
     */
    private double mSpeed;
    private volatile float mStartAngle = 0;

    /**
     * 是否点击了停止
     */
    private boolean isShouldEnd;

    /**
     * 控件的中心位置
     */
    private int mCenter;

    /**
     * 控件的padding，这里我们认为4个padding的值一致，以paddingleft为标准
     */
    private int mPadding;

    /**
     * 背景图的bitmap
     */
    private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(),
            R.drawable.bg2);
    /**
     * 文字的大小
     */
    private float mTextSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());


    public LuckDicsView(Context context) {
        super(context);
    }

    public LuckDicsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);
        //设置可以聚焦
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置常亮
        this.setKeepScreenOn(true);
    }

    /**
     * 设置转盘所需元素
     * @param awardCount 奖品项数
     * @param award 奖品名称
     * @param awardImgs 奖品背景图片
     * @param itemBgColor 奖品对应扇形块的背景图片
     */
    public void setElement(int awardCount, String[] award, int[] awardImgs, int[] itemBgColor){
        this.mItemCount = awardCount;
        this.award = award;
        this.awardImgs = awardImgs;
        this.mColors = itemBgColor;
    }

    /**
     * 设置控件为正方形
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //以屏幕的较小边距为基准
        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
        // 获取圆形的直径
        mRadius = width - getPaddingLeft() - getPaddingRight();
        // padding值
        mPadding = getPaddingLeft();
        // 中心点
        mCenter = width / 2;
        setMeasuredDimension(width, width);
    }

    /**
     * 在创建时激发，一般在这里调用画图的线程。
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //初始化绘制圆弧的画笔
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);
        //绘制文字的画笔
        mTextPaint = new Paint();
        mTextPaint.setColor(0xFFffffff);
        mTextPaint.setTextSize(mTextSize);
        //圆弧的绘制范围,以左上角为基准
        mRange = new RectF(getPaddingLeft(), getPaddingLeft(), mRadius
                + getPaddingLeft(), mRadius + getPaddingLeft());
        mImgsBitmap = new Bitmap[mItemCount];
        for(int i = 0; i < mItemCount; i++){
            mImgsBitmap[i] = BitmapFactory.decodeResource(getResources(),awardImgs[i]);
        }

        //开启线程
        isRunning = true;
        mThread = new Thread(this);//下面有实现run方法
        mThread.start();
    }

    /**
     * 在surface的大小发生改变时激发
     * @param holder
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * 销毁时激发，一般在这里将画图的线程停止、释放。
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }

    /**
     * 实现Runnable里的方法
     */
    @Override
    public void run(){
        while(isRunning){
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            try {
                if(end - start < 50){
                    Thread.sleep(50 - (end - start));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 绘图
     */
    private void draw(){
        try{
            mCanvas = mHolder.lockCanvas();//锁定画布对象
            if(mCanvas != null){//开始作画
                drawBg();
                //绘制每个盘块，每个盘块里面的文字，每个盘块上的图片
                float tempAngle = mStartAngle;//角度
                float sweepAngle = (float) (360 / mItemCount);//每个盘块的角度
                for(int i = 0; i < mItemCount; i++){
                    //绘制盘块
                    mArcPaint.setColor(mColors[i]);
                    mCanvas.drawArc(mRange, tempAngle, sweepAngle, true,
                            mArcPaint);
                    //绘制文本
                    drawText(tempAngle,sweepAngle,award[i]);
                    //绘制icon
                    drawIcon(tempAngle, i);

                    tempAngle += sweepAngle;
                }
                //如果mSpeed不为0，则认为圆盘滚动了
                mStartAngle += mSpeed;
                // 点击停止时，设置mSpeed为递减，为0值转盘停止
                if(isShouldEnd){
                    mSpeed -= 1;
                }
                if(mSpeed <= 0){
                    mSpeed = 0;
                    isShouldEnd = false;
                }
                // 根据当前旋转的mStartAngle计算当前滚动到的区域
                calInExactArea(mStartAngle);
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(mCanvas != null){
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    /**
     * 绘制背景
     */
    private void drawBg(){
        mCanvas.drawColor(0xFFFFFFFF);
        mCanvas.drawBitmap(mBgBitmap, null, new Rect(mPadding / 2,
                mPadding / 2, getMeasuredWidth() - mPadding / 2,
                getMeasuredWidth() - mPadding / 2), null);
    }

    /**
     * 绘制文字
     利用Path，添加入一个Arc，然后设置水平和垂直的偏移量，垂直偏移量就是当前Arc朝着圆心移动的距离；水平偏移量，就是顺时针去旋转，
     我们偏移了 (mRadius * Math.PI / mItemCount / 2 - textWidth / 2);目的是为了文字居中。mRadius * Math.PI 是圆的周长；周长/ mItemCount / 2 是每个Arc的一半的长度；
     拿Arc一半的长度减去textWidth / 2，就把文字设置居中了。
     最后，用过path去绘制文本即可。
     * @param startAngle
     * @param sweepAngle
     * @param text
     */
    private void drawText(float startAngle, float sweepAngle, String text){
        Path path = new Path();
        path.addArc(mRange,startAngle, sweepAngle);
        float textWidth = mTextPaint.measureText(text);
        // 利用水平偏移让文字居中
        float hOffset = (float) (mRadius * Math.PI / mItemCount / 2 - textWidth / 2);// 水平偏移
        float vOffset = mRadius / 2 / 6;// 垂直偏移
        mCanvas.drawTextOnPath(text, path, hOffset, vOffset, mTextPaint);
    }

    /**
     * 绘制图标
     * @param startAngle
     * @param i
     */
    private void drawIcon(float startAngle, int i){
        // 设置图片的宽度为直径的1/8
        int imgWidth = mRadius / 8;
        float angle = (float) ((30 + startAngle) * (Math.PI / 180));

        int x = (int) (mCenter + mRadius / 2 / 2 * Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2 / 2 * Math.sin(angle));

        // 确定绘制图片的位置
        Rect rect = new Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth
                / 2, y + imgWidth / 2);

        mCanvas.drawBitmap(mImgsBitmap[i], null, rect, null);
    }

    /**
     * 根据当前旋转的mStartAngle计算当前滚动到的区域
     * @param startAngle
     */
    private void calInExactArea(float startAngle){
        float rotate = startAngle + 90;
        rotate %= 360.0;
        for(int i = 0; i < mItemCount; i++){
            // 每个的中奖范围
            // 每个的中奖范围
            float from = 360 - (i + 1) * (360 / mItemCount);
            float to = from + 360 - (i) * (360 / mItemCount);
            if ((rotate > from) && (rotate < to))
            {
                Log.i("TAG", award[i]);
                return;
            }
        }
    }

    /**
     * 点击开始
     * @param luckyIndex 最终是停留在哪个选项上
     */
    public void luckyStart(int luckyIndex){
        //每项角度大小
        float angle = (float)(360 / mItemCount);
        // 中奖角度范围（因为指针向上，所以水平第一项旋转到指针指向，需要旋转210-270；）
        float from = 270 - (luckyIndex + 1) * angle;
        float to = from + angle;
        //停下来时旋转的大小
        float targetFrom = 4 * 360 + from;
        /**
         * <pre>
         *  等差公式求和 =（首项+末项）*（项数）/ 2
         *  (v1 + 0) * (v1+1) / 2 = target ;
         *  v1*v1 + v1 - 2target = 0 ;
         *  v1=-1+(1*1 + 8 *1 * target)/2;
         * </pre>
         * v1和v2确保每次停下来的位置是随机的
         */
        float v1 = (float) (Math.sqrt(1 * 1 + 8 * 1 * targetFrom) - 1) / 2;
        float targetTo = 4 * 360 + to;
        float v2 = (float) (Math.sqrt(1 * 1 + 8 * 1 * targetTo) - 1) / 2;

        mSpeed = (float) (v1 + Math.random() * (v2 - v1));
        isShouldEnd = false;
    }

    /**
     * 停止滚动
     */
    public void luckyEnd(){
        mStartAngle = 0;
        isShouldEnd = true;
    }

    public boolean isStart()
    {
        return mSpeed != 0;
    }

    public boolean isShouldEnd()
    {
        return isShouldEnd;
    }
}