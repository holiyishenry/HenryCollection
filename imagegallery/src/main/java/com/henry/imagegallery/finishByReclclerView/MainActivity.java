package com.henry.imagegallery.finishByReclclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.henry.imagegallery.R;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class MainActivity extends Activity
{

	private MoodRecyclerView mRecyclerView;
	private MoodGalleryAdapter mAdapter;
	private List<Integer> mDatas;
    private boolean isSelected[];
//    private ImageView mFrontView, mMiddleView, mNextView;
    private ImageView mMoodImageView[];

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		

		mDatas = new ArrayList<Integer>(Arrays.asList(R.drawable.emo_baobao_07,R.drawable.emo_baobao_01,
				R.drawable.emo_baobao_02, R.drawable.emo_baobao_03,  R.drawable.emo_baobao_04,
				R.drawable.emo_baobao_05, R.drawable.emo_baobao_06, R.drawable.emo_baobao_07, R.drawable.emo_baobao_01));
        isSelected = new boolean[mDatas.size()];
        mMoodImageView = new ImageView[mDatas.size()];
		mRecyclerView = (MoodRecyclerView) findViewById(R.id.id_recyclerview_horizontal);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

		mRecyclerView.setLayoutManager(linearLayoutManager);
		mAdapter = new MoodGalleryAdapter(this, mDatas);
		mRecyclerView.setAdapter(mAdapter);
        /**
         * 心情图集的滑动事件
         */
		mRecyclerView.setOnItemScrollChangeListener(new MoodRecyclerView.OnItemScrollChangeListener()
		{
			@Override
			public void onChange(View frontView, View middleView, View nextView, int position)
			{
//无单击效果时，仅仅知道当时情况即可
//                mFrontView = (ImageView)frontView.findViewById(R.id.id_index_gallery_item_image);
//                mMiddleView =  (ImageView)middleView.findViewById(R.id.id_index_gallery_item_image);
//                mNextView =  (ImageView)nextView.findViewById(R.id.id_index_gallery_item_image);
//                shrinkImage(mFrontView, position - 1);
//                shrinkImage(mNextView, position + 1);
//                expanseImage(mMiddleView, position);
//由于需要单击效果，需要缓冲ImageView，这里采用数组来缓冲
                if(mMoodImageView[position - 1] == null)
                    mMoodImageView[position - 1] = (ImageView)frontView.findViewById(R.id.id_index_gallery_item_image);
                if(mMoodImageView[position] == null)
                    mMoodImageView[position] = (ImageView)middleView.findViewById(R.id.id_index_gallery_item_image);
                if(mMoodImageView[position + 1] == null)
                    mMoodImageView[position + 1] = (ImageView)nextView.findViewById(R.id.id_index_gallery_item_image);
                shrinkImage(mMoodImageView[position - 1], position - 1);
                shrinkImage(mMoodImageView[position + 1], position + 1);
                expanseImage(mMoodImageView[position], position);
			};
		});

        /**
         * 表情的单击事件
         */
		mAdapter.setOnItemClickLitener(new MoodGalleryAdapter.OnItemClickLitener()
		{
			@Override
			public void onItemClick(View view, int position)
			{
                if(position == 0){
                    shrinkImage(mMoodImageView[position + 1], position + 1);
                    expanseImage(mMoodImageView[position], position);
                }else if(position == mDatas.size() - 1){
                    shrinkImage(mMoodImageView[position - 1], position - 1);
                    expanseImage(mMoodImageView[position], position);
                }else{
                    shrinkImage(mMoodImageView[position - 1], position - 1);
                    shrinkImage(mMoodImageView[position + 1], position + 1);
                    expanseImage(mMoodImageView[position], position);
                }
			}
		});

	}

    /**
     * 放大心情图片
     * @param view
     */
    private void expanseImage(View view, int position){
        if(!isSelected[position]){
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.5f),
                    ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.5f));
            set.setDuration(400).start();
            isSelected[position] = true;
        }
    }

    /**
     * 收缩心情图片
     */
    private void shrinkImage(View view, int position){
        if(isSelected[position]){//如果已经被放大了，则需要缩小
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(view, "scaleX", 1.5f, 1f),
                    ObjectAnimator.ofFloat(view, "scaleY", 1.5f, 1f));
            set.setDuration(400).start();
            isSelected[position] = false;
        }
    }

}
