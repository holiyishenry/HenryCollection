package com.holiy.sidingmenuV6;


import com.holiy.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

public class GuideHintActivity extends Activity {
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_hint);
        sharedPreferences = getSharedPreferences("shade_read_status", MODE_WORLD_READABLE);
    }

    public void close(View view){
        Editor editor = sharedPreferences.edit();
        // 存入数据
        editor.putBoolean("read",true);
        // 提交修改
        editor.commit();
        setResult(RESULT_OK);
        finish();
    }

    @Override
      public boolean onTouchEvent(MotionEvent event) {
        setResult(RESULT_OK);
        finish();
        return true;
    }

}
