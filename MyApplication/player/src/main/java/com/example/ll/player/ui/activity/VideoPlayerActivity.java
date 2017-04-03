package com.example.ll.player.ui.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import com.example.ll.player.R;
import com.example.ll.player.bean.VideoBean;

public class VideoPlayerActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "VideoPlayerActivity";

    private VideoView mVideoview;
    private Button mBtnPlayPause;

    @Override
    public int getLayout() {
        return R.layout.activity_video_player;
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        VideoBean bean = (VideoBean) intent.getSerializableExtra("bean");
        // 设置需要播放的uri
        mVideoview.setVideoURI(Uri.parse(bean.getData()));
    }

    @Override
    public void initView() {
        mVideoview = (VideoView) findViewById(R.id.videoview);
        mBtnPlayPause = (Button) findViewById(R.id.btn_video_play_pause);
    }

    @Override
    public void initListener() {
        mBtnPlayPause.setOnClickListener(this);
        mVideoview.setOnPreparedListener(new VideoOnPreparedListener());
    }

    @Override
    public boolean getHideTitleBar() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_video_play_pause:
                switchPlayPause();
                break;
        }
    }

    // 切换播放，暂停
    private void switchPlayPause() {
        if (mVideoview.isPlaying()){
            mVideoview.pause();
        }else {
            mVideoview.start();
        }
        updatePlayPasue();
    }

    // 更新播放按钮
    private void updatePlayPasue() {
        if (mVideoview.isPlaying()){
            // 正在播放显示暂停按钮
            mBtnPlayPause.setBackgroundResource(R.drawable.selector_pause);
        }else {
            mBtnPlayPause.setBackgroundResource(R.drawable.selector_play);
        }
    }

    // 视频准备完成监听
    private class VideoOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            // 视频准备完成后播放
            mVideoview.start();
            // 进行更新按钮显示
            updatePlayPasue();
        }
    }
}
