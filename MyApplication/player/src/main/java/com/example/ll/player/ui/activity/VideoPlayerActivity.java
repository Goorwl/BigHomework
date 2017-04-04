package com.example.ll.player.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ll.player.R;
import com.example.ll.player.bean.VideoBean;
import com.example.ll.player.utils.StringUtils;

import java.util.ArrayList;

public class VideoPlayerActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "VideoPlayerActivity";
    private static final int SET_SYSTEM_TIME = 0;
    private static final int MSG_PALY_POSITION = 1;
    private static final int MSG_HIDE_CTRL = 2;

    private com.example.ll.player.view.VideoView mVideoview;
    private Button mBtnPlayPause;
    private TextView mTvTitle;
    private TextView mTvTime;
    private ImageView mIvBattery;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SET_SYSTEM_TIME:
                    startSystemTime();
                    break;
                case MSG_PALY_POSITION:
                    updatePlayPosition();
                    break;
                case MSG_HIDE_CTRL:
                    switchCtrl();
                    break;
            }
        }
    };
    private BatteryBroadcastReceiver mReceiver;
    private SeekBar mSbVolume;
    private AudioManager mAudioManager;
    private ImageView mIvMute;
    private int mCurrentVolume;
    private int mCurrentVolume1;
    private float mStartY;
    private WindowManager mWindowManager;
    private int mHeight;
    private TextView mTvTotaltime;
    private TextView mTvProgresstime;
    private SeekBar mSbplay;
    private VideoBean mBean;
    private Button mBtnBack;
    private Button mBtnPre;
    private Button mBtnNext;
    private Button mBtnFullScreen;
    private int mPosition;
    private ArrayList<VideoBean> mList;
    private GestureDetector mGestureDetector;
    private LinearLayout mLlBottom;
    private LinearLayout mLlTop;
    private LinearLayout mLlLoading;

    @Override
    public int getLayout() {
        return R.layout.activity_video_player;
    }

    @Override
    public void initData() {
        Intent intent = getIntent();

        Uri data = intent.getData();
        if (data == null) {     // 来自跳转
            mList = (ArrayList<VideoBean>) intent.getSerializableExtra("list");
            mPosition = intent.getIntExtra("position", -1);
            playItem();
        } else {     // 来自sdcard
            mVideoview.setVideoURI(data);
            mTvTitle.setText(data.toString());
            mBtnNext.setEnabled(false);
            mBtnPre.setEnabled(false);
        }
        // 初始化系统时间
        startSystemTime();
        // 监听电池电量变化
        setlisetenBatteryChanged();

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 设置系统音量关联
        mSbVolume.setMax(maxVolume);
        int currentVolume = getCurrentVolume();
        // 设置默认的系统音量
        mSbVolume.setProgress(currentVolume);

        // 获取系统屏幕
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mHeight = mWindowManager.getDefaultDisplay().getHeight();

        mTvTotaltime.setText(StringUtils.formaterTime(mBean.getTime()));
    }

    // 播放视频
    private void playItem() {
        mBtnPre.setEnabled(mPosition != 0);
        mBtnNext.setEnabled(mPosition != mList.size() - 1);
        mBean = mList.get(mPosition);
        // 设置需要播放的uri
        mVideoview.setVideoURI(Uri.parse(mBean.getData()));
        mTvTitle.setText(mBean.getTitle());
    }

    // 获取系统当前音量
    private int getCurrentVolume() {
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    private void setlisetenBatteryChanged() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mReceiver = new BatteryBroadcastReceiver();
        registerReceiver(mReceiver, filter);
    }

    // 设置系统时间
    private void startSystemTime() {
        mTvTime.setText(StringUtils.systemTime());
        mHandler.sendEmptyMessageDelayed(SET_SYSTEM_TIME, 500);      //最小执行间隔为一秒，小于一秒时间会变慢
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除所有消息
        mHandler.removeCallbacksAndMessages(null);
        // 广播反注册
        unregisterReceiver(mReceiver);
    }

    @Override
    public void initView() {
        mVideoview = (com.example.ll.player.view.VideoView) findViewById(R.id.videoview);
        mBtnPlayPause = (Button) findViewById(R.id.btn_video_play_pause);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvTime = (TextView) findViewById(R.id.tv_titme);
        mIvBattery = (ImageView) findViewById(R.id.iv_battery);
        mSbVolume = (SeekBar) findViewById(R.id.seekBar);
        mIvMute = (ImageView) findViewById(R.id.iv_voice);

        mTvTotaltime = (TextView) findViewById(R.id.tv_player_totaltime);
        mTvProgresstime = (TextView) findViewById(R.id.tv_player_progresstime);
        mSbplay = (SeekBar) findViewById(R.id.sb_player);

        mBtnBack = (Button) findViewById(R.id.btn_video_back);
        mBtnPre = (Button) findViewById(R.id.btn_video_pre);
        mBtnNext = (Button) findViewById(R.id.btn_video_next);
        mBtnFullScreen = (Button) findViewById(R.id.btn_video_screen);

        mLlBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        mLlTop = (LinearLayout) findViewById(R.id.ll_top);

        mLlLoading = (LinearLayout) findViewById(R.id.ll_loading);
    }

    @Override
    public void initListener() {
        mBtnPlayPause.setOnClickListener(this);
        mVideoview.setOnPreparedListener(new VideoOnPreparedListener());
        mSbVolume.setOnSeekBarChangeListener(new SeekBarChangeListener());
        mIvMute.setOnClickListener(this);
        mSbplay.setOnSeekBarChangeListener(new SeekBarChangeListener());
        mVideoview.setOnCompletionListener(new OnCompletionListener());
        mVideoview.setOnInfoListener(new OnInfoListener());
        mVideoview.setOnBufferingUpdateListener(new OnBufferingUpdateListener());

        mBtnBack.setOnClickListener(this);
        mBtnPre.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mBtnFullScreen.setOnClickListener(this);

        mGestureDetector = new GestureDetector(new SimpleOnGestureListener());
    }

    @Override
    public boolean getHideTitleBar() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_video_play_pause:
                switchPlayPause();
                break;
            case R.id.iv_voice:
                switchVoiceMute();
                break;
            case R.id.btn_video_back:
                finish();
                break;
            case R.id.btn_video_pre:
                playPre();
                break;
            case R.id.btn_video_next:
                playNext();
                break;
            case R.id.btn_video_screen:
                switchFullScreen();
                break;
        }
    }

    // 切换全屏功能
    private void switchFullScreen() {
        mVideoview.switchDefaultFullScreen();
        updateFullScreen();
    }

    // 更新图标显示
    private void updateFullScreen() {
        if (mVideoview.isFullScreen()) {
            mBtnFullScreen.setBackgroundResource(R.drawable.selector_default_screen);
        } else {
            mBtnFullScreen.setBackgroundResource(R.drawable.selector_fullscreen);
        }
    }

    // 播放上一曲
    private void playPre() {
        if (mPosition != 0) {
            mPosition--;
            playItem();
        }
    }

    // 播放下一曲
    private void playNext() {
        if (mPosition != mList.size() - 1) {
            mPosition++;
            playItem();
        }
    }

    // 切换静音状态
    private void switchVoiceMute() {
        int current = getCurrentVolume();
        // ==0 表示当前静音
        if (current == 0) {
            updateVolumeMute(mCurrentVolume);
        } else {
            mCurrentVolume = getCurrentVolume();
            updateVolumeMute(0);
        }
    }

    // 滑动屏幕更新音量
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 绑定手势识别器
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 初始值获取当前点击的坐标，同时获取当前系统音量
                mStartY = event.getY();
                mCurrentVolume1 = getCurrentVolume();
                break;
            case MotionEvent.ACTION_MOVE:
                /*
                * 1. 最终音量= 滑动音量+初始音量
                * 2.滑动音量=屏幕百分比*最大音量
                * 3.屏幕百分比=滑动距离*屏幕高度
                * 4.滑动距离= 移动位置-按下位置
                * */
                float endY = event.getY();
                float moveY = mStartY - endY;
                float precent = moveY / mHeight;
                float v = precent * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int finalVolume = (int) (mCurrentVolume1 + v);
                updateVolumeMute(finalVolume);
                break;
        }

        return super.onTouchEvent(event);
    }

    // 更新静音切换功能
    private void updateVolumeMute(int progress) {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
        mSbVolume.setProgress(progress);
    }

    // 切换播放，暂停
    private void switchPlayPause() {
        if (mVideoview.isPlaying()) {
            mVideoview.pause();
        } else {
            mVideoview.start();
        }
        updatePlayPasue();
    }

    // 更新播放按钮
    private void updatePlayPasue() {
        if (mVideoview.isPlaying()) {
            // 正在播放显示暂停按钮
            mBtnPlayPause.setBackgroundResource(R.drawable.selector_pause);
            mHandler.sendEmptyMessageDelayed(MSG_PALY_POSITION, 500);
        } else {
            mBtnPlayPause.setBackgroundResource(R.drawable.selector_play);
        }
    }

    // 视频准备完成监听
    private class VideoOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            // 准备完成移除加载中的提示界面
            mLlLoading.setVisibility(View.INVISIBLE);
            // 视频准备完成后播放
            mVideoview.start();
            // 更新按钮显示
            updatePlayPasue();
            // 更新播放时间
            updatePlayPosition();
            // 关联视频播放的进度条
            mSbplay.setMax(mBean.getTime());
            // 准备完成，三秒后自动隐藏控制面板
            notifyAutoHideCtrl();
        }
    }

    // 三秒之后自动隐藏
    private void notifyAutoHideCtrl() {
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_CTRL, 2900);
    }

    // 更新播放进度
    private void updatePlayPosition() {
        int currentPosition = mVideoview.getCurrentPosition();
        updatePlaytime(currentPosition);
        mHandler.sendEmptyMessageDelayed(MSG_PALY_POSITION, 500);
    }

    // 更新播放时间
    private void updatePlaytime(int currentPosition) {
        mTvProgresstime.setText(StringUtils.formaterTime(currentPosition));
        mSbplay.setProgress(currentPosition);
    }

    // 动态广播监听电量变化
    private class BatteryBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", -1);
            updaterbatteryIcon(level);
        }
    }

    //更新系统电池电量图标
    private void updaterbatteryIcon(int level) {
        if (level == 100) {
            mIvBattery.setImageResource(R.drawable.ic_battery_100);
        } else if (level >= 80) {
            mIvBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level >= 60) {
            mIvBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level >= 40) {
            mIvBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level >= 20) {
            mIvBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level >= 10) {
            mIvBattery.setImageResource(R.drawable.ic_battery_10);
        } else {
            mIvBattery.setImageResource(R.drawable.ic_battery_0);
        }
    }

    // 设置seekbar滑动监听事件
    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // 不是用户操作时候，直接返回
            if (!fromUser) {
                return;
            }
            // TODO: 2017/4/4 修改静音图标
//            if (progress == 0){
//                mIvMute.setImageResource();
//            }

            switch (seekBar.getId()) {
                case R.id.seekBar:
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    break;
                case R.id.sb_player:
                    mVideoview.seekTo(progress);
                    updatePlaytime(progress);
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeMessages(MSG_HIDE_CTRL);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            notifyAutoHideCtrl();
        }
    }

    // 视频播放完成回调方法
    private class OnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mHandler.removeMessages(MSG_PALY_POSITION);
            //更新播放暂停按钮
            updatePlayPasue();
            int duration = mp.getDuration();
            updatePlaytime(duration);
        }
    }

    // 手势识别器，判断单击还是双击
    private class SimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            // 长安播放暂停
            switchPlayPause();
            super.onLongPress(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // 双击全屏切换
            switchFullScreen();
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // 单击隐藏显示面板
            switchCtrl();
            return super.onSingleTapConfirmed(e);
        }
    }

    private boolean isShowing = true;

    // 隐藏显示面板
    private void switchCtrl() {
        if (isShowing) {
            com.nineoldandroids.view.ViewPropertyAnimator.animate(mLlTop).translationY(-mLlTop.getHeight());
            com.nineoldandroids.view.ViewPropertyAnimator.animate(mLlBottom).translationY(mLlBottom.getHeight());
            notifyAutoHideCtrl();
        } else {
            com.nineoldandroids.view.ViewPropertyAnimator.animate(mLlTop).translationY(0);
            com.nineoldandroids.view.ViewPropertyAnimator.animate(mLlBottom).translationY(0);
        }
        isShowing = !isShowing;
    }

    // 开始缓冲的回调
    private class OnInfoListener implements MediaPlayer.OnInfoListener {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    mLlLoading.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    mLlLoading.setVisibility(View.INVISIBLE);
                    break;
            }
            return false;
        }
    }

    // 设置第二缓冲
    private class OnBufferingUpdateListener implements MediaPlayer.OnBufferingUpdateListener {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            float pre = percent / 100f;
            int pro = (int) (pre * mp.getDuration());
            mSbplay.setSecondaryProgress(pro);
        }
    }
}
