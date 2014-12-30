package com.henry.tablewidget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.henry.tablewidget.R;
import com.henry.tablewidget.TableWidgetManager;

/**
 * 浮动圆球桌面默认信息
 */
public class TableWidgetFloatView_old extends LinearLayout {
	private ImageView windowMenu;
	private TableWidgetManager manager;

	public int mWidth;
	public int mHeight;

	private int preX;
	private int preY;
	private int x;
	private int y;

	public TableWidgetFloatView_old(Context context) {
		this(context, null);
	}

	public TableWidgetFloatView_old(Context context, AttributeSet attrs) {
		super(context, attrs);
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
			preX = (int) event.getRawX();
			preY = (int) event.getRawY();
//			isMove = false;
			break;
		case MotionEvent.ACTION_MOVE:
			x = (int) event.getRawX();
			y = (int) event.getRawY();
			manager.move(this, x - preX, y - preY);

			preX = x;
			preY = y;
			break;
		case MotionEvent.ACTION_UP:
			if(Math.abs(x - preX) < 5 && Math.abs(y - preY) < 5) {//是否是移动，主要是区分click
				//显示悬浮框详细流量信息
				manager.showContent();
			}
			break;
		}
		return super.onTouchEvent(event);
	}
}
