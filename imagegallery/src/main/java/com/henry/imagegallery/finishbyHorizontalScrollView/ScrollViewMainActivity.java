package com.henry.imagegallery.finishbyHorizontalScrollView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.henry.imagegallery.R;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class ScrollViewMainActivity extends Activity
{
    private HorizontalScrollView imageScrollView;
	private LinearLayout mGallery;
    /**
     * 表情id
     */
	private int[] mMoodImgIds;
    /**
     * 表情imageview
     */
    private ImageView mMoodImageView[];
    /**
     * 标记表情是否被选中
     */
    private boolean isSelectedMood[];
	private LayoutInflater mInflater;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_scrollview);
		mInflater = LayoutInflater.from(this);
		initData();
		initView();
	}

	private void initData()
	{
        mMoodImgIds = new int[] { R.drawable.emo_baobao_01, R.drawable.emo_baobao_02, R.drawable.emo_baobao_03,
				R.drawable.emo_baobao_04, R.drawable.emo_baobao_05, R.drawable.emo_baobao_06,
                R.drawable.emo_baobao_07 };
        mMoodImageView = new ImageView[mMoodImgIds.length];
        isSelectedMood  = new boolean[mMoodImgIds.length];
	}

	private void initView()
	{
		mGallery = (LinearLayout) findViewById(R.id.id_gallery);
        imageScrollView = (HorizontalScrollView)findViewById(R.id.image_horizontal_scrollview);
		for (int i = 0; i < mMoodImgIds.length; i++)
		{
			View view = mInflater.inflate(R.layout.activity_index_gallery_item,
					mGallery, false);
            mMoodImageView[i] = (ImageView) view
                .findViewById(R.id.id_index_gallery_item_image);
            mMoodImageView[i].setImageResource(mMoodImgIds[i]);
            mMoodImageView[i].setOnClickListener(new ImageOnClickListener(i));
			TextView txt = (TextView) view
					.findViewById(R.id.id_index_gallery_item_text);
			txt.setText("some info ");
			mGallery.addView(view);
		}
//        mGallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                for(int i = 0; i < mMoodImgIds.length; i++){
//                    if(isSelectedMood[i]){
//                        shrinkImage(mMoodImageView[i]);
//                        isSelectedMood[i] = false;
//                    }
//                }
//            }
//        });
        mGallery.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        for (int i = 0; i < mMoodImgIds.length; i++) {
                            if (isSelectedMood[i]) {
                                shrinkImage(mMoodImageView[i]);
                                isSelectedMood[i] = false;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.e("x", "x:");
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });
	}

    private class ImageOnClickListener implements View.OnClickListener {
//        ImageView img;
        int position;
        ImageOnClickListener(int position){
//            this.img = img;
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            expanseImage(v);
            isSelectedMood[position] = true;
        }
    }

    /**
     * 放大图片
     * @param view
     */
    private void expanseImage(View view){
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.5f),
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.5f));
        set.setDuration(500).start();
    }

    /**
     * 收缩图片
     */
    private void shrinkImage(View view){
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 1.5f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 1.5f, 1f));
        set.setDuration(500).start();
    }
}
