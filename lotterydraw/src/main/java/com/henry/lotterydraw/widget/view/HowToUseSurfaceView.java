package com.henry.lotterydraw.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by holiy on 2014/12/17.
 */
public class HowToUseSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {


    public HowToUseSurfaceView(Context context) {
        super(context);
    }

    public HowToUseSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    /**
     * 在创建时激发，一般在这里调用画图的线程。
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

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

    }

    /**
     * 实现绘图线程
     */
    @Override
    public void run() {
        while(true)//判定条件
        {
            draw();
        }
    }

    /**
     * 绘图工具
     */
    private void draw(){

    }
}
