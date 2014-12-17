package com.henry.lotterydraw.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.henry.lotterydraw.R;
import com.henry.lotterydraw.widget.view.LuckDicsView;

public class MainActivity extends Activity {
    private LuckDicsView luckDicsView;
    private ImageView mStartBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView(){
        luckDicsView = (LuckDicsView) findViewById(R.id.id_luckypan);
        mStartBtn = (ImageView) findViewById(R.id.id_start_btn);
        //奖品数量
        int awardCount = 8;
        //奖品
        String[] award = new String[] { "单反相机", "IPAD", "恭喜发财", "IPHONE",
                "妹子一只", "恭喜发财" ,"单反相机","恭喜发财"};
        //奖品图标
        int[] awardImgs = new int[] { R.drawable.danfan, R.drawable.ipad,
                R.drawable.f040, R.drawable.iphone, R.drawable.meizi,
                R.drawable.f040, R.drawable.danfan, R.drawable.f040,};
        //每个盘块对应的背景颜色
        int[] bgColors = new int[] { 0xFFFFC300, 0xFFF17E01, 0xFFFFC300,
                0xFFF17E01, 0xFFFFC300, 0xFFF17E01,0xFFFFC300, 0xFFF17E01};
        luckDicsView.setElement(awardCount, award, awardImgs, bgColors);

        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!luckDicsView.isStart()){
                    mStartBtn.setImageResource(R.drawable.stop);
                    luckDicsView.luckyStart(4);
                }else{
                    if(!luckDicsView.isShouldEnd()){
                        mStartBtn.setImageResource(R.drawable.start);
                        luckDicsView.luckyEnd();
                    }
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
