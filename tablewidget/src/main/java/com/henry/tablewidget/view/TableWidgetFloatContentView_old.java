package com.henry.tablewidget.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.TrafficStats;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.henry.tablewidget.R;
import com.henry.tablewidget.TableWidgetManager;
import com.henry.tablewidget.util.SizeFormaterUtil;

import java.util.List;

/**
 * 浮动窗口显示的内容,查看软件的流量使用情况
 */
public class TableWidgetFloatContentView_old extends LinearLayout implements OnClickListener {

	public int mWidth;
	public int mHeight;
	private TableWidgetManager manager;
	private PackageManager pkgManager;
	private LayoutInflater inflater;
	private TrafficAdapter adapter;
	private Button btCLose;
	private Button btBack;
	private ListView listView;

	public TableWidgetFloatContentView_old(Context context) {
		this(context, null);
	}

	public TableWidgetFloatContentView_old(Context context, AttributeSet attrs) {
		super(context, attrs);
		pkgManager = context.getPackageManager();
		// 填充布局，并添加至
		inflater = LayoutInflater.from(context);

        inflater.inflate(R.layout.float_content_view, this);
		manager = TableWidgetManager.getInstance(context);

		ViewGroup.LayoutParams params = findViewById(R.id.content)
				.getLayoutParams();
		mWidth = params.width;
		mHeight = params.height;

		btCLose = (Button) findViewById(R.id.bt_close);
		btBack = (Button) findViewById(R.id.bt_back);
		listView = (ListView) findViewById(R.id.list);

		btCLose.setOnClickListener(this);
		btBack.setOnClickListener(this);

		adapter = new TrafficAdapter();
		listView.setAdapter(adapter);
		//消息，主要完成流量的实时更新
		handler.sendEmptyMessage(0);
	}

	// 主要用于实时更新
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 更新流信息
			if (manager.isUpdate()) {
				adapter.notifyDataSetChanged();
				handler.sendEmptyMessageDelayed(0, 2 * 1000);
			}
		}
	};

	@Override
	public void onClick(View v) {
		if (v == btCLose) {
			manager.dismiss();
		} else if (v == btBack) {
			manager.back();
		}
	}

	private class TrafficAdapter extends BaseAdapter {

		private List<ResolveInfo> infos;

		TrafficAdapter() {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			//根据意图，查询得到与之对应的list信息
			infos = pkgManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		}

		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public Object getItem(int position) {
			return infos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item, null);
				convertView.setTag(new ViewHolder(convertView));
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();

			ResolveInfo info = infos.get(position);
			holder.tvPkgName.setText(info.loadLabel(pkgManager).toString());
            try {
				//得到uid
				int uid = pkgManager.getPackageInfo(info.activityInfo.packageName,
						0).applicationInfo.uid;
				//TrafficStats查询具体流量数据
				holder.tvRcvSize.setText(SizeFormaterUtil.getDataSize(TrafficStats
                        .getUidRxBytes(uid)));
				holder.tvSndSize.setText(SizeFormaterUtil.getDataSize(TrafficStats
                        .getUidTxBytes(uid)));
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return convertView;
		}

	}

	private class ViewHolder {
		TextView tvPkgName;
		TextView tvRcvSize;
		TextView tvSndSize;

		public ViewHolder(View view) {
			tvPkgName = (TextView) view.findViewById(R.id.tv_name);
			tvRcvSize = (TextView) view.findViewById(R.id.tv_rcv);
			tvSndSize = (TextView) view.findViewById(R.id.tv_snd);
		}
	}

}
