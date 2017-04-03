package com.example.ll.player.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * Created by wule on 2017/4/3.
 * video数据bean类
 */

public class VideoBean implements Serializable{
    private String title;
    private int time;
    private String data;
    private long size;

    public static VideoBean fromCursor(Cursor cursor) {

        VideoBean videoBean = new VideoBean();

        if (cursor == null || cursor.getCount() == 0) {
            // 没有数据返回空对象
            return videoBean;
        }
        videoBean.size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
        videoBean.title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
        videoBean.time = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
        videoBean.data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        return videoBean;
    }

    @Override
    public String toString() {
        return "VideoBean{" +
                "title='" + title + '\'' +
                ", time=" + time +
                ", data='" + data + '\'' +
                ", size=" + size +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
