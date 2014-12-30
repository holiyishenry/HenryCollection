package com.henry.tablewidget;

import com.henry.tablewidget.view.TableWidgetFloatContentView;
import com.henry.tablewidget.view.TableWidgetFloatView;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

/**
 * 单例设计, 用于将自定义的view 放到WindowManage中，显示到桌面上
 */
public class TableWidgetManager {

	private static TableWidgetManager manager;

	private TableWidgetManager() {
	}

	private static WindowManager winManager;
	private static Context context;
	public LayoutParams params;
	private LayoutParams paramsContent;
	/**
	 * 显示的浮动窗体
	 */
	private TableWidgetFloatView floatView;
	/**
	 * 点击浮动窗体后的详细信息
	 */
	private TableWidgetFloatContentView floatContentView;

	// 屏幕的尺寸
	public static int displayWidth;
	public static int displayHeight;

	/**
	 * @param context
	 *            ApplicationContext
	 * @return
	 */
	public static synchronized TableWidgetManager getInstance(Context context) {
		if (manager == null) {
			TableWidgetManager.context = context;
			winManager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			displayWidth = winManager.getDefaultDisplay().getWidth();
			displayHeight = winManager.getDefaultDisplay().getHeight();

			manager = new TableWidgetManager();
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
        if(floatContentView != null){
            //将floatContentView里面的menu位置和当前floatMenu的位置重叠
            floatContentView.setMenuButtonParams(floatContentView.mMenuButton,params.x,params.y);
        }
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
	 * @return
	 */
	public TableWidgetFloatView getView() {
		if (floatView == null) {
			floatView = new TableWidgetFloatView(context);
		}
		if (params == null) {
			params = new LayoutParams();
			params.type = LayoutParams.TYPE_PHONE;
			params.format = PixelFormat.RGBA_8888;
			params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
					| LayoutParams.FLAG_NOT_FOCUSABLE;
			params.gravity = Gravity.LEFT | Gravity.TOP;

			// 须指定宽度高度信息
			params.width = floatView.mWidth;
			params.height = floatView.mHeight;
			params.x = displayWidth - floatView.mWidth;
			params.y = displayHeight / 2;

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
			floatContentView = new TableWidgetFloatContentView(context);
		}


		if (paramsContent == null) {
			paramsContent = new LayoutParams();
			paramsContent.type = LayoutParams.TYPE_PHONE;
			paramsContent.format = PixelFormat.RGBA_8888;
			paramsContent.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
					| LayoutParams.FLAG_NOT_FOCUSABLE;
//			paramsContent.gravity = Gravity.LEFT | Gravity.TOP;

			// 须指定宽度高度信息
//			paramsContent.width = floatContentView.mWidth;
//			paramsContent.height = floatContentView.mHeight;
//			paramsContent.x = (displayWidth - floatContentView.mWidth) / 2;
//			paramsContent.y = (displayHeight - floatContentView.mHeight) / 2;

            paramsContent.width = displayWidth;
			paramsContent.height = displayHeight;
			paramsContent.x = 0;
			paramsContent.y = 0;
		}

		return floatContentView;
	}
}
