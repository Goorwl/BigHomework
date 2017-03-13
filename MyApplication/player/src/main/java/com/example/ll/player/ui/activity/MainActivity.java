package com.example.ll.player.ui.activity;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.ll.player.R;
import com.example.ll.player.adapter.MainAdapter;
import com.example.ll.player.ui.fragment.AudioFragment;
import com.example.ll.player.ui.fragment.VideoFragment;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final int TABS_VIDEO = 0;
    private static final int TABS_AUDIO = 1;

    private static final String TAG = "MainActivity";
    private ViewPager mViewpage;
    private List<Fragment> mList;
    private TextView mTvVideo;
    private TextView mTvAudio;
    private View mIndictor;

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {

        //设置指示器的宽度
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        mIndictor.getLayoutParams().width = width / 2;
    }

    @Override
    public void initView() {
        mViewpage = (ViewPager) findViewById(R.id.vp);
        mTvVideo = (TextView) findViewById(R.id.tv_video);
        mTvAudio = (TextView) findViewById(R.id.tv_audio);
        mIndictor = findViewById(R.id.indictor);
        //初始化显示标签
        updateTitle(TABS_VIDEO);
    }

    @Override
    public void initListener() {
        //添加标签点击事件
        mTvVideo.setOnClickListener(this);
        mTvAudio.setOnClickListener(this);

        //添加内容
        mList = new ArrayList<>();
        mList.add(new VideoFragment());
        mList.add(new AudioFragment());
        mViewpage.setAdapter(new MainAdapter(getSupportFragmentManager(),mList));

        mViewpage.setOnPageChangeListener(new PageChangeListener());
    }

    @Override
    public boolean getHideTitleBar() {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_audio:
                updateTitle(TABS_AUDIO);
                switchItem(TABS_AUDIO);
                break;
            case R.id.tv_video:
                updateTitle(TABS_VIDEO);
                switchItem(TABS_VIDEO);
                break;
        }
    }

    //切换列表界面
    private void switchItem(int tabsVideo) {
        mViewpage.setCurrentItem(tabsVideo);
    }

    //给页面变化添加监听事件
    private class PageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            LogUtils.e(TAG,positionOffset + "");
            int width = mIndictor.getWidth();
            //起始位置
            int startX = position * width;
            //移动距离
            float moveX = width * positionOffset;
            //最终位置
            int endX = (int) (startX + moveX);

            ViewHelper.setTranslationX(mIndictor,endX);
        }

        @Override
        public void onPageSelected(int position) {
            updateTitle(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    //更新标题栏
    private void updateTitle(int position) {
        if (position == TABS_AUDIO){
            mTvAudio.setTextColor(getResources().getColor(R.color.green));
            mTvVideo.setTextColor(getResources().getColor(R.color.half_white));

            ViewPropertyAnimator.animate(mTvAudio).scaleX(1.2f);
            ViewPropertyAnimator.animate(mTvAudio).scaleY(1.2f);

            ViewPropertyAnimator.animate(mTvVideo).scaleX(1f);
            ViewPropertyAnimator.animate(mTvVideo).scaleY(1f);
        }else {
            mTvVideo.setTextColor(getResources().getColor(R.color.green));
            mTvAudio.setTextColor(getResources().getColor(R.color.half_white));

            ViewPropertyAnimator.animate(mTvVideo).scaleX(1.2f);
            ViewPropertyAnimator.animate(mTvVideo).scaleY(1.2f);

            ViewPropertyAnimator.animate(mTvAudio).scaleX(1f);
            ViewPropertyAnimator.animate(mTvAudio).scaleY(1f);
        }
    }
}
