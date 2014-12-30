package com.henry.tablewidget;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 创建桌面浮动图标的服务，具备同步更新桌面控件数据的功能
 */
public class TableWidgetService extends Service {

	private TableWidgetManager windowManager;
	/*
	 * 这里使用线程池,比如访问网络数据的实时更新，主要完成于悬浮框的通信
	 */
	private ScheduledExecutorService threadPool;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (windowManager == null) {
			windowManager = TableWidgetManager
					.getInstance(getApplicationContext());
		}

		if (threadPool == null) {
			threadPool = Executors.newScheduledThreadPool(1);
			threadPool.scheduleAtFixedRate(command, 0, 1, TimeUnit.SECONDS);
		}
		//开启悬浮框
		windowManager.show();
		return super.onStartCommand(intent, flags, startId);
	}

    //获取数据，更新ui，线程， 这种方式是通过主动的定时请求来获取更新，
    // 另一种方式，是在activity中获取到更新后通过子线程来更新桌面控件ui
	private Runnable command = new Runnable() {
		@Override
		public void run() {
			// 在该方法中，定时更新ui，比如访问网络数据的实时更新，主要完成于悬浮框的通信，这里暂不实现

		}
	};

	public void onDestroy() {
		super.onDestroy();

		if (threadPool != null) {
			threadPool.shutdown();
			threadPool = null;
		}
	}
}
