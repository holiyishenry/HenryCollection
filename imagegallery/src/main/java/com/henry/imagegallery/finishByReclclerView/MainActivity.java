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
import android.widget.TextView;

import com.henry.imagegallery.R;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class MainActivity extends Activity
{

	private MoodRecyclerView mRecyclerView;
	private MoodGalleryAdapter mAdapter;
	private List<Integer> mDatas;
    private String moodDescription[];
    private boolean isSelected[];
//    private ImageView mFrontView, mMiddleView, mNextView;
//    private ImageView mMoodImageView[];
    private MoodItemView mMoodItemView[];
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		

		mDatas = new ArrayList<Integer>(Arrays.asList(R.drawable.emo_baobao_07,R.drawable.emo_baobao_01,
				R.drawable.emo_baobao_02, R.drawable.emo_baobao_03,  R.drawable.emo_baobao_04,
				R.drawable.emo_baobao_05, R.drawable.emo_baobao_06, R.drawable.emo_baobao_07, R.drawable.emo_baobao_01));
        moodDescription = new String[]{"发怒","小幸福","无语","好悲伤","小嘚瑟","好棒","生气了","火气好大","小幸福"};
        isSelected = new boolean[mDatas.size()];
//        mMoodImageView = new ImageView[mDatas.size()];
        mMoodItemView = new MoodItemView[mDatas.size()];
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
//version1无单击效果时，仅仅知道当时情况即可
//                mFrontView = (ImageView)frontView.findViewById(R.id.id_index_gallery_item_image);
//                mMiddleView =  (ImageView)middleView.findViewById(R.id.id_index_gallery_item_image);
//                mNextView =  (ImageView)nextView.findViewById(R.id.id_index_gallery_item_image);
//                shrinkImage(mFrontView, position - 1);
//                shrinkImage(mNextView, position + 1);
//                expanseImage(mMiddleView, position);

//version2由于需要单击效果，需要缓冲ImageView，这里采用数组来缓冲,没有描叙表情
//                if(mMoodImageView[position - 1] == null)
//                    mMoodImageView[position - 1] = (ImageView)frontView.findViewById(R.id.id_index_gallery_item_image);
//                if(mMoodImageView[position] == null)
//                    mMoodImageView[position] = (ImageView)middleView.findViewById(R.id.id_index_gallery_item_image);
//                if(mMoodImageView[position + 1] == null)
//                    mMoodImageView[position + 1] = (ImageView)nextView.findViewById(R.id.id_index_gallery_item_image);
//                shrinkImage(mMoodImageView[position - 1], position - 1);
//                shrinkImage(mMoodImageView[position + 1], position + 1);
//                expanseImage(mMoodImageView[position], position);

//version3
                if(mMoodItemView[position - 1] == null){
                    mMoodItemView[position - 1] = new MoodItemView();
                    mMoodItemView[position - 1].moodImage = (ImageView)frontView.findViewById(R.id.id_index_gallery_item_image);
                    //图片心情描述
                    mMoodItemView[position - 1].moodInfo = (TextView)frontView.findViewById(R.id.id_index_gallery_item_text);
                    mMoodItemView[position - 1].moodInfo.setText(moodDescription[position - 1]);
                }
                if(mMoodItemView[position] == null){
                    mMoodItemView[position] = new MoodItemView();
                    mMoodItemView[position].moodImage = (ImageView)middleView.findViewById(R.id.id_index_gallery_item_image);
                    //图片心情描述
                    mMoodItemView[position].moodInfo = (TextView)middleView.findViewById(R.id.id_index_gallery_item_text);
                    mMoodItemView[position].moodInfo.setText(moodDescription[position]);
                }
                if(mMoodItemView[position + 1] == null){
                    mMoodItemView[position + 1] = new MoodItemView();
                    mMoodItemView[position + 1].moodImage = (ImageView)nextView.findViewById(R.id.id_index_gallery_item_image);
                    //图片心情描述
                    mMoodItemView[position + 1].moodInfo = (TextView)nextView.findViewById(R.id.id_index_gallery_item_text);
                    mMoodItemView[position + 1].moodInfo.setText(moodDescription[position + 1]);
                }

                shrinkImage(mMoodItemView[position - 1], position - 1);
                shrinkImage(mMoodItemView[position + 1], position + 1);
                expanseImage(mMoodItemView[position], position);


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
//                version2
//                if(position == 0){
//                    shrinkImage(mMoodImageView[position + 1], position + 1);
//                    expanseImage(mMoodImageView[position], position);
//                }else if(position == mDatas.size() - 1){
//                    shrinkImage(mMoodImageView[position - 1], position - 1);
//                    expanseImage(mMoodImageView[position], position);
//                }else{
//                    shrinkImage(mMoodImageView[position - 1], position - 1);
//                    shrinkImage(mMoodImageView[position + 1], position + 1);
//                    expanseImage(mMoodImageView[position], position);
//                }

//                version3
                if(position == 0){
                    shrinkImage(mMoodItemView[position + 1], position + 1);
                }else if(position == mDatas.size() - 1){
                    shrinkImage(mMoodItemView[position - 1], position - 1);
                }else{
                    shrinkImage(mMoodItemView[position - 1], position - 1);
                    shrinkImage(mMoodItemView[position + 1], position + 1);
                }
                expanseImage(mMoodItemView[position], position);
			}
		});

	}

    /**
     * version2
     * 放大心情图片
     * @param view
     */
//    private void expanseImage(View view, int position){
//        if(!isSelected[position]){
//            AnimatorSet set = new AnimatorSet();
//            set.playTogether(
//                    ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.5f),
//                    ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.5f));
//            set.setDuration(400).start();
//            isSelected[position] = true;
//        }
//    }

    /**
     *Version3
     * 放大心情图片
     * @param item
     */
    private void expanseImage(MoodItemView item, int position){
        if(item == null){
            return;
        }
        item.moodInfo.setVisibility(View.VISIBLE);//隐藏文字
        if(!isSelected[position]){
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(item.moodImage, "scaleX", 1f, 1.5f),
                    ObjectAnimator.ofFloat(item.moodImage, "scaleY", 1f, 1.5f));
            set.setDuration(400).start();
            isSelected[position] = true;
        }
    }

    /**
     * version2
     * 收缩心情图片
     */
//    private void shrinkImage(View view, int position){
//        if(isSelected[position]){//如果已经被放大了，则需要缩小
//            AnimatorSet set = new AnimatorSet();
//            set.playTogether(
//                    ObjectAnimator.ofFloat(view, "scaleX", 1.5f, 1f),
//                    ObjectAnimator.ofFloat(view, "scaleY", 1.5f, 1f));
//            set.setDuration(400).start();
//            isSelected[position] = false;
//        }
//    }
    /**
     * version3
     * 收缩心情图片
     */
    private void shrinkImage(MoodItemView item, int position){
        if(item == null){
            return;
        }
        item.moodInfo.setVisibility(View.INVISIBLE);//隐藏文字
        if(isSelected[position]){//如果已经被放大了，则需要缩小
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(item.moodImage, "scaleX", 1.5f, 1f),
                    ObjectAnimator.ofFloat(item.moodImage, "scaleY", 1.5f, 1f));
            set.setDuration(400).start();
            isSelected[position] = false;
        }
    }

    private class MoodItemView{
        ImageView moodImage;
        TextView moodInfo;
    }

}
