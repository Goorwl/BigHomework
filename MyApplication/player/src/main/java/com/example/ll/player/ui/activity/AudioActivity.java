package com.example.ll.player.ui.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ll.player.R;
import com.example.ll.player.bean.AudioBean;
import com.example.ll.player.lyric.LyricView;
import com.example.ll.player.ui.service.AudioService;
import com.example.ll.player.ui.service.IAdudioMethod;
import com.example.ll.player.utils.StringUtils;

import java.util.ArrayList;

/*
* 只做界面展示
* */
public class AudioActivity extends BaseActivity implements View.OnClickListener {

    private static final int MSG_UPDATE_PLAY_TIME = 0;
    private static final int MSG_ROLL_LYRIC = 1;
    private ArrayList<AudioBean> mList;
    private ServiceConnection mConnection;
    private AudioService mService;
    private ImageView mIvBack;
    private TextView mTvTitle;
    private ImageView mIvWave;
    private SeekBar mSbAudio;
    private Button mBtnMode;
    private Button mBtnPre;
    private Button mBtnPlayPause;
    private Button mBtnNext;
    private MyBroadcastReceiver mMyBroadcastReceiver;
    private TextView mTvTime;
    private LyricView mLyricView;
    private AnimationDrawable mAd;


    @Override
    public int getLayout() {
        return R.layout.activity_audio;
    }

    @Override
    public void initData() {
        Intent intent = new Intent(getIntent());
        intent.setClass(this, AudioService.class);
        startService(intent);

        mConnection = new ServiceConnection();
        bindService(intent, mConnection, BIND_AUTO_CREATE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(AudioService.ACTION_PREPARE);
        mMyBroadcastReceiver = new MyBroadcastReceiver();
        registerReceiver(mMyBroadcastReceiver, filter);

        //开启帧动画
        mAd = (AnimationDrawable) mIvWave.getBackground();
        mAd.start();
    }

    @Override
    public void initView() {
        mIvBack = (ImageView) findViewById(R.id.iv_audio_back);
        mTvTitle = (TextView) findViewById(R.id.tv_audio_title);
        mIvWave = (ImageView) findViewById(R.id.iv_wave);
        mSbAudio = (SeekBar) findViewById(R.id.sb_audio_progress);
        mBtnMode = (Button) findViewById(R.id.btn_play_mode);
        mBtnPre = (Button) findViewById(R.id.btn_pre);
        mBtnPlayPause = (Button) findViewById(R.id.btn_play_pause);
        mBtnNext = (Button) findViewById(R.id.btn_next);
        mTvTime = (TextView) findViewById(R.id.tv_play_total_time);
        mLyricView = (LyricView) findViewById(R.id.lyric_view);
//        Button btnList = findViewById(R.id.btn)
    }

    @Override
    public void initListener() {
        mBtnPlayPause.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
        mSbAudio.setOnSeekBarChangeListener(new OnSeekBarChangeListener());
        mBtnPre.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mBtnMode.setOnClickListener(this);
    }

    @Override
    public boolean getHideTitleBar() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play_pause:
                switchPlayPause();
                break;
            case R.id.iv_audio_back:
                finish();
                break;
            case R.id.btn_pre:
                mService.playPre();
                break;
            case R.id.btn_next:
                mService.playNext();
                break;
            case R.id.btn_play_mode:
                switchPlayMode();
                break;

        }
    }

    // 点击切换播放模式
    private void switchPlayMode() {
        mService.switchPlayMode();
        updatePlayModeStatues();
    }

    // 修改播放模式图片
    private void updatePlayModeStatues() {
        switch (mService.getPlayMode()) {
            case AudioService.PLAY_MODE_ALL:
                mBtnMode.setBackgroundResource(R.drawable.audio_playmode_repate_all_selector);
                break;
            case AudioService.PLAY_MODE_REPEAT:
                mBtnMode.setBackgroundResource(R.drawable.audio_playmode_repate_single_selector);
                break;
            case AudioService.PLAY_MODE_RANDOM:
                mBtnMode.setBackgroundResource(R.drawable.audio_playmode_repate_random_selector);
                break;
        }
    }

    // 切换播放暂停
    private void switchPlayPause() {
        mService.switchPlayPause();
        updatePlayPause();
    }

    // 准备完成发送广播
    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updatePlayPause();
            AudioBean bean = (AudioBean) intent.getSerializableExtra("bean");

            String name = bean.getName();
            //设置音乐的标题
            mTvTitle.setText(name);
            // 关联seekbar
            mSbAudio.setMax(mService.getTotalTime());

            startPlayTime();

            // 修改显示图标
            updatePlayModeStatues();

            // 从歌词文件中加载歌词
//            File file = new File("/sdcard/Download/" + name);
//            mLyricView.parseLyrics(file);
            mLyricView.parseLyrics(name);

            // 滚动歌词
            rollLyric();
        }
    }

    private void rollLyric() {
        mLyricView.roll(mService.getCurrentTime(), mService.getTotalTime());
        // 不写时间表示实时处理消息
        mHandler.sendEmptyMessage(MSG_ROLL_LYRIC);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_PLAY_TIME:
                    startPlayTime();
                    break;
                case MSG_ROLL_LYRIC:
                    rollLyric();
                    break;
            }
        }
    };

    // 设置播放时间
    private void startPlayTime() {
        int currentTime = mService.getCurrentTime();
        updatePlayTime(currentTime);
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PLAY_TIME, 500);
    }

    // 修改播放时间，修改进度条进度
    private void updatePlayTime(int currentTime) {
        int totalTime = mService.getTotalTime();
        mSbAudio.setProgress(currentTime);
        mTvTime.setText(StringUtils.formaterTime(currentTime) + "/" + StringUtils.formaterTime(totalTime));
    }

    // 更新 暂停 播放按钮
    private void updatePlayPause() {
        if (mService.isPlaying()) {
            mBtnPlayPause.setBackgroundResource(R.drawable.audio_pause_selector);
            mAd.start();
        } else {
            mAd.stop();
            mBtnPlayPause.setBackgroundResource(R.drawable.audio_play_selector);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        unregisterReceiver(mMyBroadcastReceiver);
    }

    // 混合开启服务，需要一个连接对象
    class ServiceConnection implements android.content.ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IAdudioMethod method = (IAdudioMethod) service;
            mService = method.getAudioService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    // 设置音乐播放进度条
    private class OnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }
            mService.seekTo(progress);
            updatePlayTime(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
