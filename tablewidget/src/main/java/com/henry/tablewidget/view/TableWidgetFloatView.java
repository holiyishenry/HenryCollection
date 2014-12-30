package com.henry.tablewidget.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.henry.tablewidget.R;
import com.henry.tablewidget.TableWidgetManager;

/**
 * 浮动圆球桌面默认信息
 */
public class TableWidgetFloatView extends LinearLayout {
    private ImageView windowMenu;
    private TableWidgetManager manager;

    public int mWidth;
    public int mHeight;

    /**
     * 刚刚点击菜单时的坐标
     */
    private int startX, startY;
    /**
     * 菜单按钮最终定格坐标
     */
    private int lastX,lastY;

    private TableWidgetFloatView tableWidgetFloatView;

    public TableWidgetFloatView(Context context) {
        this(context, null);
    }

    public TableWidgetFloatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        tableWidgetFloatView  = this;
        // 填充布局，并添加至
        LayoutInflater.from(context).inflate(R.layout.float_view, this);
        windowMenu = (ImageView) findViewById(R.id.window_menu_iv);

        //宽高
        mWidth = windowMenu.getLayoutParams().width;
        mHeight = windowMenu.getLayoutParams().height;

        manager = TableWidgetManager.getInstance(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = lastX = (int) event.getRawX();
                startY = lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                manager.move(this, x - lastX, y - lastY);
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
                if(Math.abs(lastX - startX) < 5 && Math.abs(lastY - startY) < 5) {//是否是移动，主要是区分click
                    //显示悬浮框详细流量信息
                    manager.showContent();
                }else{
//                      粗暴的移动效果，太恶心
//                    if(lastX < manager.displayWidth / 2){
//                        manager.params.x = 0;
//                        manager.move(this, 0, 0);
//                    }else{
//                        manager.params.x = 0;
//                        manager.move(this, manager.displayWidth - mWidth , 0);
//                    }
                    moveToSide();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    // 用于更新ui
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            manager.params.x = 0;
            manager.move(tableWidgetFloatView, msg.what, 0);
        }
    };
    /**
     * 平滑的将控件移动到两边
     */
    private void moveToSide(){
         new Thread(new Runnable() {
             @Override
             public void run() {
                 int speed = 2;
                 //靠左
                 if(lastX < manager.displayWidth / 2){
                     for(int i = lastX; i > 0; i -= speed){
                         if(i == lastX / 2 || i == lastX / 3 ){
                             speed++;//加快速度，达到先慢后快的效果
                         }
                         try {
                             Thread.sleep(1);
                         } catch (InterruptedException e) {
                             e.printStackTrace();
                         }
                         handler.sendEmptyMessage(i);
                     }
                 }else{//靠右
                     for(int i = lastX ; i <= manager.displayWidth - mWidth; i += speed ){
                         if(i == (manager.displayWidth - mWidth) / 2 || i == (manager.displayWidth - mWidth) / 3){
                             speed++;//加快速度，达到先慢后快的效果
                         }
                         try {
                             Thread.sleep(1);
                         } catch (InterruptedException e) {
                             e.printStackTrace();
                         }
                         handler.sendEmptyMessage(i);
                     }
                 }
             }
         }).start();
    }

}
