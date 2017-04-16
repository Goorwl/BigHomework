package com.example.ll.player.ui.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.example.ll.player.R;
import com.example.ll.player.bean.AudioBean;
import com.example.ll.player.ui.activity.AudioActivity;
import com.example.ll.player.utils.SPUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class AudioService extends Service {

    public static final String ACTION_PREPARE = "com.example.ll.player.ui.ACTION_PREPARE";
    private static final int NOTIFY_CONTENT = 0;
    private static final int NOTIFY_NEXT = 1;
    private static final int NOTIFY_PRE = 2;
    private int mGetMode;

    public int getPlayMode() {
        return playMode;
    }

    public void setPlayMode(int playMode) {
        this.playMode = playMode;
    }

    public static final int PLAY_MODE_ALL = 0;
    public static final int PLAY_MODE_REPEAT = 1;
    public static final int PLAY_MODE_RANDOM = 2;

    private String notifyType = "type";

    // 默认值
    private int playMode = PLAY_MODE_ALL;

    private MediaPlayer mMediaPlayer;
    private ArrayList<AudioBean> mList;
    private int mPosition;
    private NotificationManager nm;

    public AudioService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Music();
    }

    // 切换开始 暂停
    public void switchPlayPause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            removeNotify();
        } else {
            mMediaPlayer.start();
            showNotify();
        }
    }

    // 切换播放模式
    public void switchPlayMode() {
        switch (playMode) {
            case PLAY_MODE_ALL:
                playMode = PLAY_MODE_REPEAT;
                break;
            case PLAY_MODE_REPEAT:
                playMode = PLAY_MODE_RANDOM;
                break;
            case PLAY_MODE_RANDOM:
                playMode = PLAY_MODE_ALL;
                break;
        }
        // 存储播放模式的值
        SPUtils.putInt(this, "mode", playMode);
    }

    // 根据播放模式自动播放下一首歌
    private void autoPlayNext() {
        switch (playMode) {
            case PLAY_MODE_ALL:
                if (mPosition == mList.size() - 1) {
                    mPosition = 0;
                } else {
                    mPosition++;
                }
                break;
            case PLAY_MODE_REPEAT:
                // 单曲循环，不需要修改位置
                break;
            case PLAY_MODE_RANDOM:
                Random random = new Random();
                int i = random.nextInt(mList.size() - 1);
                mPosition = i;
                break;
        }
        playItem();
    }

    // 根据播放模式自动播放上一首歌
    private void autoPlayPre() {
        switch (playMode) {
            case PLAY_MODE_ALL:
                if (mPosition == 0) {
                    mPosition = mList.size() - 1;
                } else {
                    mPosition--;
                }
                break;
            case PLAY_MODE_REPEAT:
                // 单曲循环，不需要修改位置
                break;
            case PLAY_MODE_RANDOM:
                Random random = new Random();
                int i = random.nextInt(mList.size() - 1);
                mPosition = i;
                break;
        }
        playItem();
    }

    // 修改进度条进度
    public void seekTo(int progress) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(progress);
        }
    }

    // 播放上一曲
    public void playPre() {
        if (mPosition != 0) {
            mPosition--;
            playItem();
        } else {
//            Toast.makeText(this, "已经是第一首歌了~~~", Toast.LENGTH_SHORT).show();
            autoPlayPre();
        }
    }

    // 播放下一曲
    public void playNext() {
        if (mPosition != mList.size() - 1) {
            mPosition++;
            playItem();
        } else {
//            Toast.makeText(this, "已经是最后一首歌了~~~", Toast.LENGTH_SHORT).show();
            autoPlayNext();
        }
    }

    class Music extends Binder implements IAdudioMethod {

        @Override
        public AudioService getAudioService() {
            return AudioService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        playMode = SPUtils.getInt(this, "mode", 0);
        mMediaPlayer = new MediaPlayer();

        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int type = intent.getIntExtra(notifyType, -1);
        switch (type) {
            case NOTIFY_CONTENT:
                notifyUpdateUI();
                break;
            case NOTIFY_NEXT:
                playNext();
                break;
            case NOTIFY_PRE:
                playPre();
                break;
            case -1:        //  从播放列表跳转过来的
            default:
                int position = intent.getIntExtra("position", -1);
                if (position == mPosition) {        //相同歌曲不切换
                    notifyUpdateUI();
                }else {
                    mList = (ArrayList<AudioBean>) intent.getSerializableExtra("list");
                    mPosition = position;
                    playItem();
                }
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    // 开始播放
    private void playItem() {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.reset();
            }
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(mList.get(mPosition).getData());
            mMediaPlayer.setOnCompletionListener(new OnCompletionListener());
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(new OnPreparedListener());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();

        // 播放时候显示通知栏
        showNotify();
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    // 音乐准备完成发送
    private class OnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            notifyUpdateUI();
        }
    }

    private void notifyUpdateUI() {
        Intent intent = new Intent();
        intent.setAction(ACTION_PREPARE);
        intent.putExtra("bean", mList.get(mPosition));
        sendBroadcast(intent);
    }

    // 获取音频总时间
    public int getTotalTime() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    // 获取当前时间
    public int getCurrentTime() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    // 播放完成的监听
    private class OnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            autoPlayNext();
        }
    }


    /**
     * 移除通知栏
     */
    public void removeNotify() {
        nm.cancel(0);
    }

    /**
     * 显示通知栏
     */
    public void showNotify() {
        Notification.Builder builder = new Notification.Builder(this);
        //状态来
        builder.setTicker("正在播放：" + mList.get(mPosition).getName());
        builder.setSmallIcon(R.drawable.notification_music_playing);

        //通知栏
        builder.setContent(getRemoteView());
        builder.setContentIntent(getPendingIntent());

        Notification notification = builder.build();

        //显示通知栏
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0, notification);
    }

    /**
     * 获取自定义通知栏的布局
     */
    private RemoteViews getRemoteView() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notify_audio);
        remoteViews.setTextViewText(R.id.tv_notify_title, mList.get(mPosition).getName());
        remoteViews.setOnClickPendingIntent(R.id.iv_notify_pre, getPrePendingIntent());
        remoteViews.setOnClickPendingIntent(R.id.iv_notify_next, getNextPendingIntent());
        return remoteViews;
    }

    /**
     * 点击通知栏，打开音乐播放界面
     */
    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, AudioActivity.class);
        intent.putExtra(notifyType, NOTIFY_CONTENT);
        PendingIntent pi = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }

    private PendingIntent getNextPendingIntent() {
        Intent intent = new Intent(this, AudioService.class);
        intent.putExtra(notifyType, NOTIFY_NEXT);
        PendingIntent pi = PendingIntent.getService(this, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }

    private PendingIntent getPrePendingIntent() {
        Intent intent = new Intent(this, AudioService.class);
        intent.putExtra(notifyType, NOTIFY_PRE);
        PendingIntent pi = PendingIntent.getService(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }

}
