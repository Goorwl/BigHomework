package com.example.ll.player.utils;

import android.util.Log;

/**
 * Description : 日志工具类，上线之后关闭
 * Copyright   : (c) 2016
 * Author      : Goorwl
 * Email       : goorwl@163.com
 * Date        : 2017/3/12 17:11
 */

public class LogUtils {
    //标志位，打包前关闭
    private static boolean isLog = true;

    public void i(String TAG ,String msg){
        if (isLog){
            Log.i(TAG, msg);
        }
    }
    public void e(String TAG ,String msg){
        if (isLog){
            Log.e(TAG, msg);
        }
    }
    public void w(String TAG ,String msg){
        if (isLog){
            Log.w(TAG, msg);
        }
    }
    public void v(String TAG ,String msg){
        if (isLog){
            Log.v(TAG, msg);
        }
    }
    public void d(String TAG ,String msg){
        if (isLog){
            Log.d(TAG, msg);
        }
    }
}
