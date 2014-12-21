package com.henry.wipelottery;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
/**
 * 刮刮乐自定义类
 * Created by holiy on 2014/12/20.
 */
public class WipelotteryView extends View {

    /**
     * 绘制线条的Paint,即用户手指绘制Path，绘制擦除时的线条
     */
    private Paint mFingerPaint = new Paint();
    /**
     * 记录用户绘制的Path，擦除时的路线
     */
    private Path mPath = new Path();
    /**
     * 内存中创建的Canvas
     */
    private Canvas mCanvas;
    /**
     * mCanvas绘制内容在其上
     */
    private Bitmap mBitmap;
    /**
     * 背景底图
     */
    private Bitmap backBitmap;
    /**
     * 刮奖表面图片
     */
    private Bitmap surfaceBitmap;
    /**
     * 是否将覆盖层刮去70%，以此来判断是否完成刮奖
     */
    private boolean isComplete;
    /**
     * 奖项背景画笔
     */
    private Paint mBackPaint = new Paint();
    /**
     * 圆角框
     */
    private Rect mTextBound = new Rect();
    /**
     * 奖项文字说明
     */
    private String mText;
    /**
     * 奖项文字大小
     */
    private int mTextSize;

    private int mLastX;
    private int mLastY;
    /**
     * view的宽度
     */
    private int mWidth;
    /**
     * view的高度
     */
    private int mHeight;

    public WipelotteryView(Context context)
    {
        this(context, null);
    }

    public WipelotteryView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public WipelotteryView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        //获取自定义属性
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.WipelotteryView, defStyle, 0);
        int n = array.length();
        for(int i = 0; i < n; i++){
            int attr = array.getIndex(i);
            switch (attr){
                case R.styleable.WipelotteryView_award_background_image:
                    backBitmap = BitmapFactory.decodeResource(getResources(), array.getResourceId(attr, 0));
                break;
                case R.styleable.WipelotteryView_award_surface_imgae:
                    surfaceBitmap = BitmapFactory.decodeResource(getResources(), array.getResourceId(attr, 0));
                break;
                case R.styleable.WipelotteryView_award_text:
                    mText = array.getString(attr);
                    if(mText == null || "".equals(mText)){
                        mText = "";
                    }
                break;
                case R.styleable.WipelotteryView_award_text_size:
                    mTextSize = array.getDimensionPixelSize(attr, (int) TypedValue
                            .applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f,
                                    getResources().getDisplayMetrics()));// 默认为14sp
                break;
            }
        }
        init();
    }

    private void init()
    {
        mPath = new Path();
        setUpOutPaint();
        setUpBackPaint();

    }

    /**
     * 设置绘制背景的画笔
     */
    private void setUpBackPaint()
    {
        mBackPaint.setStyle(Paint.Style.FILL);
        mBackPaint.setTypeface(Typeface.DEFAULT);
        mBackPaint.setTextScaleX(2f);
        mBackPaint.setColor(Color.DKGRAY);
        mBackPaint.setTextSize(mTextSize);
        mBackPaint.getTextBounds(mText, 0, mText.length(), mTextBound);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        // 绘制奖项背景
        if(backBitmap != null){
            canvas.drawBitmap(backBitmap, null, new RectF(0, 0, getWidth(), getHeight()), null);
        }
        //绘制文字
        canvas.drawText(mText, getWidth() / 2 - mTextBound.width() / 2,
                getHeight() / 2 + mTextBound.height() / 2, mBackPaint);
        if (!isComplete)
        {
            drawPath();
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    /**
     * 判断是否是设置了match_parent,如果是warp_content则用图片的大小绘图
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 设置宽度
         */
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
        {
            mWidth = specSize;
        }else{
            // 由surface图片决定的宽
            int desireByImg = getPaddingLeft() + getPaddingRight()
                    + surfaceBitmap.getWidth();
            if (specMode == MeasureSpec.AT_MOST)// wrap_content
            {
                mWidth = Math.min(desireByImg, specSize);
            } else{
                mWidth = desireByImg;
            }
        }
        /***
        * 设置高度
        */
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
        {
            mHeight = specSize;
        }else{
            int desire = getPaddingTop() + getPaddingBottom()
                    + surfaceBitmap.getHeight();

            if (specMode == MeasureSpec.AT_MOST)// wrap_content
            {
                mHeight = Math.min(desire, specSize);
            } else
                mHeight = desire;
        }
        // 初始化bitmap
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        // 绘制遮盖层
        // mCanvas.drawColor(Color.parseColor("#c0c0c0"));
        mFingerPaint.setStyle(Paint.Style.FILL);
        mCanvas.drawRoundRect(new RectF(0, 0, mWidth, mHeight), 30, 30,
                mFingerPaint);
        mCanvas.drawBitmap(surfaceBitmap, null, new RectF(0, 0, mWidth, mHeight), null);
        setMeasuredDimension(mWidth, mHeight);
    }

    /**
     * 设置擦拭区域的画笔
     */
    private void setUpOutPaint()
    {
        // 设置画笔
        // mOutterPaint.setAlpha(0);
        mFingerPaint.setColor(Color.parseColor("#c0c0c0"));
        mFingerPaint.setAntiAlias(true);
        mFingerPaint.setDither(true);
        mFingerPaint.setStyle(Paint.Style.STROKE);
        mFingerPaint.setStrokeJoin(Paint.Join.ROUND); // 圆角
        mFingerPaint.setStrokeCap(Paint.Cap.ROUND); // 圆角
        // 设置画笔宽度
        mFingerPaint.setStrokeWidth(25);
    }

    /**
     * 绘制线条,橡皮擦路径
     */
    private void drawPath()
    {
        mFingerPaint.setStyle(Paint.Style.STROKE);
        mFingerPaint
                .setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        mCanvas.drawPath(mPath, mFingerPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                mPath.moveTo(mLastX, mLastY);
                break;
            case MotionEvent.ACTION_MOVE:

                int dx = Math.abs(x - mLastX);
                int dy = Math.abs(y - mLastY);

                if (dx > 3 || dy > 3)
                    mPath.lineTo(x, y);

                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                new Thread(mRunnable).start();
                break;
        }

        invalidate();
        return true;
    }

    /**
     * 统计擦除区域任务
     */
    private Runnable mRunnable = new Runnable()
    {
        private int[] mPixels;

        @Override
        public void run()
        {

            int w = getWidth();
            int h = getHeight();

            float wipeArea = 0;
            float totalArea = w * h;

            Bitmap bitmap = mBitmap;

            mPixels = new int[w * h];

            /**
             * 拿到所有的像素信息
             */
            bitmap.getPixels(mPixels, 0, w, 0, 0, w, h);

            /**
             * 遍历统计擦除的区域
             */
            for (int i = 0; i < w; i++)
            {
                for (int j = 0; j < h; j++)
                {
                    int index = i + j * w;
                    if (mPixels[index] == 0)
                    {
                        wipeArea++;
                    }
                }
            }

            /**
             * 根据所占百分比，进行一些操作
             */
            if (wipeArea > 0 && totalArea > 0)
            {
                int percent = (int) (wipeArea * 100 / totalArea);
                Log.e("TAG", percent + "");

                if (percent > 70)
                {
                    Log.e("TAG", "清除区域达到70%，下面自动清除");
                    isComplete = true;
                    postInvalidate();
                }
            }
        }
    };
}
