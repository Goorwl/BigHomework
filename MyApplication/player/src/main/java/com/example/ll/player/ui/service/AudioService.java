package com.example.ll.player.ui.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.example.ll.player.bean.AudioBean;

import java.io.IOException;
import java.util.ArrayList;

public class AudioService extends Service {

    public static final String ACTION_PREPARE = "com.example.ll.player.ui.ACTION_PREPARE";

    public int getPlayMode() {
        return playMode;
    }

    public void setPlayMode(int playMode) {
        this.playMode = playMode;
    }

    public static final int PLAY_MODE_ALL = 0;
    public static final int PLAY_MODE_REPEAT = 1;
    public static final int PLAY_MODE_RANDOM = 2;

    private int playMode = PLAY_MODE_ALL;

    private MediaPlayer mMediaPlayer;
    private ArrayList<AudioBean> mList;
    private int mPosition;

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
        } else {
            mMediaPlayer.start();
        }
    }

    // 切换播放模式
    public void switchPlayMode(){
        switch (playMode){
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
            Toast.makeText(this, "已经是第一首歌了~~~", Toast.LENGTH_SHORT).show();
        }
    }

    // 播放下一曲
    public void playNext() {
        if (mPosition != mList.size() - 1) {
            mPosition++;
            playItem();
        } else {
            Toast.makeText(this, "已经是最后一首歌了~~~", Toast.LENGTH_SHORT).show();
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
        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mList = (ArrayList<AudioBean>) intent.getSerializableExtra("list");
        mPosition = intent.getIntExtra("position", -1);

        playItem();


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
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(new OnPreparedListener());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
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
            Intent intent = new Intent();
            intent.setAction(ACTION_PREPARE);
            intent.putExtra("bean", mList.get(mPosition));
            sendBroadcast(intent);
        }
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
}
