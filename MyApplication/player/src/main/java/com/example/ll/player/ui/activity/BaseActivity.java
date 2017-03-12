package com.example.ll.player.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;

import com.example.ll.player.ui.UiInterface;

/**
 * Description : 所有activity的基类,采用MVC设计模式
 * 作用：
 *  1 规范代码结构（模版设计模式）
 *  2 提供公共的方法
 * Copyright   : (c) 2016
 * Author      : Goorwl
 * Email       : goorwl@163.com
 * Date        : 2017/3/12 15:15
 */

public abstract class BaseActivity extends FragmentActivity implements UiInterface{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar(getHideTitleBar());
        setContentView(getLayout());
        initData();
        initView();
        initListener();
//        setStaues();
    }

    //隐藏标题栏
    public void hideActionBar(boolean isHide){
        if (isHide){
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }

    //设置状态栏为透明
    public void setStaues(){
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
