package com.example.ll.player.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wule on 2017/4/3.
 * 格式化字符串工具类
 */

public class StringUtils {
    public static final int HOUR = 60 * 60 * 1000;
    public static final int SEN = 1000;
    public static final int MIN = 60 * 1000;

    public static String formaterTime(int time) {
        int h = time / HOUR;
        int m = time % HOUR / MIN;
        int s = time % MIN / SEN;
        if (h >= 1) {
            return String.format("%02d:%02d:%02d", h, m, s);
        } else {
            return String.format("%02d:%02d", m, s);
        }
    }

    // 获取当前系统时间
    public static String systemTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String format = sdf.format(new Date());
        return format;
    }

    // 截取音频名称
    public static String getTitle(String s){
        return s.substring(0,s.lastIndexOf("."));
    }
}
