package com.henry.tablewidget;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener{

	private Button btOpen;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btOpen = (Button) findViewById(R.id.bt_open);
		btOpen.setOnClickListener(this);
	}
	@Override
	public void onClick(View view) {
		//开启服务
		Intent intent = new Intent(getApplicationContext(), TableWidgetService.class);
		startService(intent);
		
		//关闭
		finish();
	}

}
