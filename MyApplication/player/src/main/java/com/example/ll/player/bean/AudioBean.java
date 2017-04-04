package com.example.ll.player.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import com.example.ll.player.utils.StringUtils;

import java.io.Serializable;

/**
 * Created by wule on 2017/4/4.
 */

public class AudioBean implements Serializable{

    private String name;
    private String data;
    private int time;
    private long size;

    public static AudioBean fromCursor(Cursor cursor) {

        AudioBean audioBean = new AudioBean();
        if (cursor == null || cursor.getCount() == 0) {
            // 没有数据返回空对象
            return audioBean;
        }
        audioBean.size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
        audioBean.name = StringUtils.getTitle(name);
        audioBean.time = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
        audioBean.data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        return audioBean;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "AudioBean{" +
                "name='" + name + '\'' +
                ", data='" + data + '\'' +
                ", time=" + time +
                ", size=" + size +
                '}';
    }
}
