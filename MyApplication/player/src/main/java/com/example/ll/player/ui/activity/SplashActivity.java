package com.example.ll.player.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;

import com.example.ll.player.R;

public class SplashActivity extends BaseActivity {
/*
    闪屏界面
 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //隐藏状态
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_splash;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
        Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                //销毁闪屏界面
                finish();
            }
        };
        mHandler.sendEmptyMessageDelayed(0,3000);
    }

    @Override
    public void initListener() {

    }

    @Override
    public boolean getHideTitleBar() {
        return false;
    }
}
