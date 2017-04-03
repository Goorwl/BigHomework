package com.example.ll.player.utils;

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
}
