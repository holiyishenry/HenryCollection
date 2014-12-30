package com.henry.tablewidget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.henry.tablewidget.view.TableWidgetFloatContentView;
import com.henry.tablewidget.view.TableWidgetFloatContentView_old;
import com.henry.tablewidget.view.TableWidgetFloatView;
import com.henry.tablewidget.view.TableWidgetFloatView_fun;

/**
 * 单例设计,出现fun是恶搞
 */
public class TableWidgetManager_fun {

	private static TableWidgetManager_fun manager;

	private TableWidgetManager_fun() {
	}

	private static WindowManager winManager;
	private static Context context;
	private LayoutParams params;
	private LayoutParams paramsContent;
	/**
	 * 显示的浮动窗体
	 */
	private TableWidgetFloatView_fun floatView;
	/**
	 * 点击浮动窗体后的详细信息
	 */
	private TableWidgetFloatContentView_old floatContentView;

	// 屏幕的尺寸
	private static int displayWidth;
	private static int displayHeight;

	/**
	 * @param context
	 *            ApplicationContext
	 * @return
	 */
	public static synchronized TableWidgetManager_fun getInstance(Context context) {
		if (manager == null) {
			TableWidgetManager_fun.context = context;
			winManager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			displayWidth = winManager.getDefaultDisplay().getWidth();
			displayHeight = winManager.getDefaultDisplay().getHeight();

			manager = new TableWidgetManager_fun();
		}
		return manager;
	}

	/**
	 * 显示悬浮框
	 */
	public void show() {
		floatView = getView();
		if (floatView.getParent() == null) {
			winManager.addView(floatView, params);
		}
	}

	/**
	 * 点击悬浮框后的显示的实时详细流量信息
	 */
	public void showContent() {
		winManager.addView(getContentView(), paramsContent);

		// 先移除悬浮框
		winManager.removeView(floatView);
		floatView = null;
	}

	// 移动悬浮框
	public void move(View view, int delatX, int deltaY) {
		if (view == floatView) {
			params.x += delatX;
			params.y += deltaY;
			// 更新floatView
			winManager.updateViewLayout(view, params);
		}
	}

	/**
	 * 移除悬浮框
	 */
	public void dismiss() {
		winManager.removeView(floatContentView);
		floatContentView = null;
	}

	public void back() {
		winManager.addView(getView(), params);

		winManager.removeView(floatContentView);
		floatContentView = null;
	}

	/**
	 * 是否实时更新流量数据，主要用于流量悬浮框是否实时更新流量数据
	 * 
	 * @return
	 */
	public boolean isUpdate() {
		if (floatContentView == null) {
			return false;
		}
		return true;
	}

	/**
	 * 得到悬浮框view
	 * 
	 * @return
	 */
	public TableWidgetFloatView_fun getView() {
		if (floatView == null) {
			floatView = new TableWidgetFloatView_fun(context);
		}
		if (params == null) {
			params = new LayoutParams();
			params.type = LayoutParams.TYPE_PHONE;
			params.format = PixelFormat.RGBA_8888;
			params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
					| LayoutParams.FLAG_NOT_FOCUSABLE;
			params.gravity = Gravity.LEFT | Gravity.TOP;

			// 须指定宽度高度信息
//			params.width = floatView.mMenuWidth;
//			params.height = floatView.mMenuHeight;
//			params.x = displayWidth - floatView.mMenuWidth;
//			params.y = displayHeight / 2;

            params.width = displayWidth;
			params.height = displayHeight;
			params.x = 0;
			params.y = 0;
		}
		return floatView;
	}

	/**
	 * 得到实时流量悬浮框view
	 * 
	 * @return
	 */
	public View getContentView() {
		if (floatContentView == null) {
			floatContentView = new TableWidgetFloatContentView_old(context);
		}
		if (paramsContent == null) {
			paramsContent = new LayoutParams();
			paramsContent.type = LayoutParams.TYPE_PHONE;
			paramsContent.format = PixelFormat.RGBA_8888;
			paramsContent.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
					| LayoutParams.FLAG_NOT_FOCUSABLE;
			paramsContent.gravity = Gravity.LEFT | Gravity.TOP;

			// 须指定宽度高度信息
			paramsContent.width = floatContentView.mWidth;
			paramsContent.height = floatContentView.mHeight;

			paramsContent.x = (displayWidth - floatContentView.mWidth) / 2;
			paramsContent.y = (displayHeight - floatContentView.mHeight) / 2;
		}
		return floatContentView;
	}
}
