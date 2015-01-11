package com.henry.applicationtemplate.entrance;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.henry.applicationtemplate.R;
import com.henry.applicationtemplate.common.storageutils.SharedPreferencesUtils;
import com.henry.applicationtemplate.indexmodules.IndexTabFragment;
import com.henry.applicationtemplate.messagemodules.MessageTabFragment;
import com.henry.applicationtemplate.personmodules.PersonInfoTabFragment;
import com.henry.applicationtemplate.thememodules.ThemeTabFragment;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;


public class MainActivity extends FragmentActivity {
    //----------------------浮动菜单start——————————————————————
    private Button mMenuButton;
    private Button mItemButton1;
    private Button mItemButton2;
    private Button mItemButton3;
    private Button mItemButton4;
    private Button mItemButton5;

    private ImageView removeIv;
    /**
     * 是否打开了菜单
     */
    private boolean mIsMenuOpen = false;
    /**
     * 是否显示了移除图标
     */
    private boolean mIsRemoveBtnOpen = false;
    /**
     * 获取屏幕的宽高
     */
    private int screenWidth, screenHeight;// 屏幕宽高
    /**
     * 刚刚点击菜单时的坐标
     */
    private int startX, startY;
    /**
     * 菜单按钮最终定格坐标
     */
    private int lastX = 0,lastY = 0;
    /**
     * menu靠边操作线程辅助变量
     */
    private int temp;
    //----------------------浮动菜单end——————————————————————
    //----------------------底部菜单start——————————————————————
    /**
     * 底部菜单
     */
    private PopupWindow mMenuPopupWindow;
    private View popupWindowParent;

    private IndexTabFragment indexTabFragment;
    private ThemeTabFragment themeTabFragment;
    private MessageTabFragment messageTabFragment;
    private PersonInfoTabFragment personInfoTabFragment;;
    private Fragment fragments[];

    private LinearLayout selectedView[];
    private TextView selectedTextView[];
    /**
     * 页码
     */
    private int page = 0;
    /**
     * 当前页面
     */
    private int currentPage = 0;
    //----------------------底部菜单end——————————————————————

    private ImageView publishContentMneu;

    private SharedPreferencesUtils sharedPreferencesUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferencesUtils = new SharedPreferencesUtils(this, "index");
        initFragment();
        initView();
        initFloatMenu();
        getDisplayInfo();
        setListener();
    }

    /**
     * 初始化每一页
     */
    private void initFragment(){
        indexTabFragment = new IndexTabFragment();
        themeTabFragment = new ThemeTabFragment();
        messageTabFragment = new MessageTabFragment();
        personInfoTabFragment = new PersonInfoTabFragment();
        fragments = new Fragment[]{indexTabFragment, themeTabFragment, messageTabFragment, personInfoTabFragment};
        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction().add(R.id.content_layout, indexTabFragment)
                .add(R.id.content_layout, personInfoTabFragment).hide(personInfoTabFragment)
                .show(indexTabFragment).commit();

    }

    /**
     * 初始化按钮等控件
     */
    private void initView(){
        selectedView = new LinearLayout[4];
        selectedView[0] = (LinearLayout)findViewById(R.id.tab_index_layout);
        selectedView[1] = (LinearLayout)findViewById(R.id.tab_theme_layout);
        selectedView[2] = (LinearLayout)findViewById(R.id.tab_message_layout);
        selectedView[3] = (LinearLayout)findViewById(R.id.tab_person_info_layout);

        selectedTextView = new TextView[4];
        selectedTextView[0] = (TextView)findViewById(R.id.index_tv);
        selectedTextView[1] = (TextView)findViewById(R.id.theme_tv);
        selectedTextView[2] = (TextView)findViewById(R.id.message_tv);
        selectedTextView[3] = (TextView)findViewById(R.id.person_tv);
        // 把第一个tab设为选中状态,用于改变背景
        selectedView[0].setSelected(true);

        publishContentMneu = (ImageView)findViewById(R.id.publish_content_menu);

    }

    private void setListener(){
        publishContentMneu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PublishGroupChatActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * 底部菜单选择事件
     * @param view
     */
    public void onTabClicked(View view) {
        switch (view.getId()){
            case R.id.tab_index_layout:
                page = 0;
                break;
            case R.id.tab_theme_layout:
                page = 1;
                break;
            case R.id.tab_message_layout:
                page = 2;
                break;
            case R.id.tab_person_info_layout:
                page = 3;
                break;
        }
        if (currentPage != page) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentPage]);
            if (!fragments[page].isAdded()) {
                trx.add(R.id.content_layout, fragments[page]);
            }
            trx.show(fragments[page]).commit();
        }
        changeTextColor(currentPage, page);
        selectedView[currentPage].setSelected(false);
        // 把当前tab设为选中状态
        selectedView[page].setSelected(true);
        currentPage = page;

    }

    /**
     * 选中item后，改变字体颜色
     */
    private void changeTextColor(int oldPage, int newPage){
        selectedTextView[oldPage].setTextColor(getResources().getColor(R.color.fragment_tab_text_unselected));
        selectedTextView[newPage].setTextColor(getResources().getColor(R.color.fragment_tab_text_selected));
    }

    /**
     * 获取屏幕信息
     */
    private void getDisplayInfo(){
        Display dis = this.getWindowManager().getDefaultDisplay();// Display类提供关于屏幕尺寸和分辨率的信息
        screenWidth = dis.getWidth();
        screenHeight = dis.getHeight();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initFloatMenu() {
        //弹出移除浮动菜单图标
        removeIv = (ImageView) findViewById(R.id.remove_img);
        //初始化popupwindow菜单
        View popupView = getLayoutInflater().inflate(R.layout.index_popupwindow_layout, null);
        mMenuPopupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mMenuPopupWindow.setFocusable(true);//获取焦点，否则无法触发事件
        mMenuPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mMenuPopupWindow.setAnimationStyle(R.style.popupMenuWindowAnim);
        popupWindowParent = findViewById(R.id.content_layout);

        //---------------------以下为初始化菜单操作---------------------------
        mMenuButton = (Button) findViewById(R.id.menu);
        mMenuButton.setOnTouchListener(new MenuButtonOnTouchListener(mMenuButton));

        mItemButton1 = (Button) findViewById(R.id.item1);
//        mItemButton1.setOnClickListener(new MenuItemOnClickListener());

        mItemButton2 = (Button) findViewById(R.id.item2);
//        mItemButton2.setOnClickListener(new MenuItemOnClickListener());

        mItemButton3 = (Button) findViewById(R.id.item3);
//        mItemButton3.setOnClickListener(new MenuItemOnClickListener());

        mItemButton4 = (Button) findViewById(R.id.item4);
//        mItemButton4.setOnClickListener(new MenuItemOnClickListener());

        mItemButton5 = (Button) findViewById(R.id.item5);
//        mItemButton5.setOnClickListener(new MenuItemOnClickListener());
    }

    /**
     * 打开菜单的动画
     * @param view 执行动画的view
     * @param index view在动画序列中的顺序
     * @param total 动画序列的个数
     * @param radius 动画半径
     */
    private void doAnimateOpen(View view, int index, int total, int radius) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
        double degree = Math.PI * index / ((total - 1) * 2);
        //根据菜单最终停留的位置，决定item打开方向
        int translationX = (int) (radius * Math.cos(degree));
        int translationY = (int) (radius * Math.sin(degree));
        //将屏幕分为四块，4种情况
        if(lastX >= screenWidth / 2 && lastY < screenHeight / 2){//右上角
            translationX = -translationX;
        }else if(lastX < screenWidth / 2 && lastY > screenHeight / 2){//左下角
            translationY = -translationY;
        }else if(lastX >= screenWidth / 2 && lastY > screenHeight / 2){//右下角
            translationX = -translationX;
            translationY = -translationY;
        }
//        Log.i(TAG, String.format("degree=%f, translationX=%d, translationY=%d",
//                degree, translationX, translationY));
        AnimatorSet set = new AnimatorSet();
        //包含平移、缩放和透明度动画
        set.playTogether(
                ObjectAnimator.ofFloat(view, "translationX", mMenuButton.getLeft(), mMenuButton.getLeft() + translationX),
                ObjectAnimator.ofFloat(view, "translationY", mMenuButton.getTop(), mMenuButton.getTop() + translationY),
                ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f),
                ObjectAnimator.ofFloat(view, "alpha", 0f, 1));
        //动画周期为500ms
        set.setDuration(1 * 500).start();
    }

    /**
     * 关闭菜单的动画,如果正在移动过程，则最终收缩的位置是可变的
     * @param view 执行动画的view
     * @param index view在动画序列中的顺序
     * @param total 动画序列的个数
     * @param radius 动画半径
     */
    private void doAnimateClose(final View view, int index, int total,
                                int radius) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
        double degree = Math.PI * index / ((total - 1) * 2);
        int translationX, translationY;
        translationX = (int) (radius * Math.cos(degree));
        translationY = (int) (radius * Math.sin(degree));
        //将屏幕分为四块，4种情况
        if(lastX >= screenWidth / 2 && lastY < screenHeight / 2){//右上角
            translationX = -translationX;
        }else if(lastX < screenWidth / 2 && lastY > screenHeight / 2){//左下角
            translationY = -translationY;
        }else if(lastX >= screenWidth / 2 && lastY > screenHeight / 2){//右下角
            translationX = -translationX;
            translationY = -translationY;
        }
//        Log.i(TAG, String.format("degree=%f, translationX=%d, translationY=%d",
//                degree, translationX, translationY));
        AnimatorSet set = new AnimatorSet();
        //包含平移、缩放和透明度动画
        set.playTogether(
                ObjectAnimator.ofFloat(view, "translationX", translationX + mMenuButton.getLeft(), mMenuButton.getLeft()),
                ObjectAnimator.ofFloat(view, "translationY", translationY + mMenuButton.getTop(), mMenuButton.getTop()),
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f),
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f),
                ObjectAnimator.ofFloat(view, "alpha", 1f, 0f));
        //为动画加上事件监听，当动画结束的时候，我们把当前view隐藏
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }
        });
        set.setDuration(1 * 500).start();
    }

    /**
     * 菜单按钮滑动事件
     */
    private class MenuButtonOnTouchListener implements View.OnTouchListener {
        private Button button;
        private int removeIvWidth, removeIvHeight;
        public MenuButtonOnTouchListener(Button mMenuButton) {
            this.button = mMenuButton;
            ViewGroup.LayoutParams removeIvParams = removeIv.getLayoutParams();
            removeIvWidth = removeIvParams.width;
            removeIvHeight = removeIvParams.height;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN://手指按下
                    startX = lastX = (int) event.getRawX();
                    startY = lastY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE://手指移动
                    //如果打开了菜单，需要关闭菜单,有跟随的幻影效果
                    if((Math.abs(lastX - startX) >= 5 || Math.abs(lastY - startY) >= 5)){
                        if(!mIsRemoveBtnOpen){
                            popupRemoveButton(removeIv, false);//弹出删除控件图标
                        }
                        if(mIsMenuOpen){//关闭菜单
                            closeMenuItem();
                        }
                    }

                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;

                    int top = v.getTop() + dy;
                    int left = v.getLeft() + dx;

                    if (top <= 0) {
                        top = 0;
                    }
                    if (top >= screenHeight - button.getHeight()) {
                        top = screenHeight - button.getHeight();
                    }
                    if (left >= screenWidth - button.getWidth()) {
                        left = screenWidth - button.getWidth();
                    }
                    if (left <= 0) {
                        left = 0;
                    }
                    v.layout(left, top, left + button.getWidth(),
                            top + button.getHeight());
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    //检查是否将浮动菜单移到了和移除图标重叠位置
                    //screenHeight - removeIvHeight - 150为removeIv的top值
                    //screenWidth / 2 - removeIvWidth / 2为left值
                    if(Math.abs(top - (screenHeight - removeIvHeight - 150)) < button.getHeight() / 2
                            && Math.abs(left - (screenWidth / 2 - removeIvWidth / 2)) < button.getWidth() / 2){
                        setFloatMenuToPopupWindow();
                    }
                    break;
                case MotionEvent.ACTION_UP://手指离开
                    if(Math.abs(lastX - startX)<= 5 && Math.abs(lastY - startY) <= 5){
                        //没有移动或者很小的移动都算点击事件
                        setItemAnim();
                    }else{
                        if(mIsRemoveBtnOpen){
                            popupRemoveButton(removeIv, true);
                        }
                        //当移动结束，手指离开时判定是否将menu放置到侧边
                        keepToSide(mMenuButton);
                    }
                    break;
            }
            return false;
        }
    }

    /**
     * MenuItem的开关
     */
    private void setItemAnim(){
        if (!mIsMenuOpen){
            openMenuItem();
        }else{
            closeMenuItem();
        }
    }

    /**
     * 打开菜单项
     */
    private void openMenuItem(){
        mIsMenuOpen = true;
        doAnimateOpen(mItemButton1, 0, 5, 250);
        doAnimateOpen(mItemButton2, 1, 5, 250);
        doAnimateOpen(mItemButton3, 2, 5, 250);
        doAnimateOpen(mItemButton4, 3, 5, 250);
        doAnimateOpen(mItemButton5, 4, 5, 250);
    }

    /**
     * 关闭菜单项
     */
    private void closeMenuItem(){
        mIsMenuOpen = false;
        doAnimateClose(mItemButton1, 0, 5, 250);
        doAnimateClose(mItemButton2, 1, 5, 250);
        doAnimateClose(mItemButton3, 2, 5, 250);
        doAnimateClose(mItemButton4, 3, 5, 250);
        doAnimateClose(mItemButton5, 4, 5, 250);
    }

    /**
     * 将菜单放置到侧边
     * @param view
     */
    private void keepToSide(final View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(view.getLeft() < screenWidth / 2 - view.getWidth() / 2){
                    for(int i = view.getLeft(); i > 0;i = i - 2){
                        temp = i;
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                view.layout(temp, view.getTop(),temp + view.getWidth(),  view.getTop() + view.getHeight());
                            }
                        });
                    }
                    /**
                     * 记录最终定位的坐标
                     */
                    sharedPreferencesUtils.put("floatMenuX",view.getLeft());
                    sharedPreferencesUtils.put("floatMenuY",view.getTop());
                }else{
                    for(int i = view.getLeft(); i < screenWidth - view.getWidth(); i+= 2){
                        temp = i;
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                view.layout(temp, view.getTop(), temp + view.getWidth(), view.getTop() + view.getHeight());
                            }
                        });
                    }
                    /**
                     * 记录最终定位的坐标
                     */
                    sharedPreferencesUtils.put("floatMenuX",view.getLeft());
                    sharedPreferencesUtils.put("floatMenuY",view.getTop());
                }
            }
        }).start();
    }

    /**
     * 滑动时需要弹出移除按钮
     * @param view 移除图标
     * @param isOpenRemoveButton 是否已经打开
     */
    private void popupRemoveButton(View view, boolean isOpenRemoveButton){
        if(view.getVisibility() != View.VISIBLE){
            view.setVisibility(View.VISIBLE);
        }
        //注意：不要直接用view.getWidth方法获取控件宽高，
        // 第一次时获取到为0，使用LayoutParams则不会出现这种情况
        ViewGroup.LayoutParams params = removeIv.getLayoutParams();
        int width = params.width, height = params.height;

        //将移除菜单从底部弹出
        AnimatorSet set = new AnimatorSet();
        int xFrom = 0 , yFrom = 0;
        int xTo = 0, yTo = 0;
        xFrom = xTo = screenWidth / 2 - width / 2;
        if(!isOpenRemoveButton){//打开
            mIsRemoveBtnOpen = true;
            yFrom = screenHeight + height;
            yTo = screenHeight - height - 150;
        }else{//关闭
            mIsRemoveBtnOpen = false;
            yFrom = screenHeight - height - 150;
            yTo = screenHeight + height;
        }
//        Log.i("remove", "xfrom:" + xFrom + ",xTo:" + xTo + ",yFrom:" + yFrom + ",yTo:" + yTo);
        set.playTogether(
                ObjectAnimator.ofFloat(view, "translationX", xFrom, xTo),
                ObjectAnimator.ofFloat(view, "translationY", yFrom, yTo));
        //持续0.5秒
        set.setDuration(1 * 500).start();
    }

    /**
     * 设置当浮动菜单碰见移除图标时，将浮动菜单隐藏至popupwindow
     */
    private void setFloatMenuToPopupWindow(){
        mMenuButton.setVisibility(View.GONE);
        if(mIsRemoveBtnOpen){
            popupRemoveButton(removeIv, true);
        }
        mMenuPopupWindow.showAtLocation(popupWindowParent, Gravity.BOTTOM, 0, 0);
    }
}
